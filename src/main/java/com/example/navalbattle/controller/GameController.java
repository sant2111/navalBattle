package com.example.navalbattle.controller;

import com.example.navalbattle.exceptions.OutOfBoundsShotException;
import com.example.navalbattle.model.*;
import com.example.navalbattle.model.ShipPlacement;
import com.example.navalbattle.view.BoardCell;
import com.example.navalbattle.view.GridBoardPane;
import com.example.navalbattle.view.GridCoordinate;
import com.example.navalbattle.view.shapes.HitMarkerView;
import com.example.navalbattle.view.shapes.ShipView;
import com.example.navalbattle.view.shapes.ShipViewFactory;
import com.example.navalbattle.view.shapes.SunkMarkerView;
import com.example.navalbattle.view.shapes.WaterMarkerView;
import javafx.animation.PauseTransition;
import com.example.navalbattle.facade.GameFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;
import com.example.navalbattle.persistence.PlayerInfoRepository;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Controlador de {@code game-view.fxml}: el tablero de combate con la
 * flota propia (a la vista) y las aguas enemigas (ocultas, se disparan
 * haciendo clic). Alterna turnos con el {@link GameEngine} inyectado y
 * pinta cada resultado con los marcadores de {@code view.shapes}.
 * <p>
 * Heurísticas de Nielsen aplicadas:
 * <ul>
 *   <li>1 — Visibilidad del estado: el rótulo de turno siempre indica de
 *       quién es el turno actual, y una pausa breve antes del disparo de
 *       la IA da la sensación de que "está pensando" en vez de un
 *       cambio instantáneo confuso.</li>
 *   <li>4 — Consistencia: los mismos marcadores (agua/tocado/hundido) y
 *       las mismas siluetas de barco de la colocación se reutilizan
 *       aquí sin cambios.</li>
 *   <li>5 — Prevención de errores: una celda ya disparada se deshabilita
 *       para que no se pueda volver a disparar sobre ella.</li>
 * </ul>
 * <p>
 * Eventos escuchados: clic en cada celda del tablero enemigo.
 * <p>
 * Métodos del modelo invocados (vía {@link GameEngine}, con
 * {@code controller.MockGameEngine} como motor de prueba temporal):
 * {@link GameEngine#shootOpponent(int, int)},
 * {@link GameEngine#playAiTurn()}.
 *
 * @author Jorge Navia
 * @author Carlos Meneses
 * @version 2.0
 */
public class GameController {

    private static final double MARKER_SIZE = BoardCell.CELL_SIZE * 0.6;
    private static final Duration AI_TURN_DELAY = Duration.millis(600);

    @FXML
    private GridBoardPane ownBoard;

    @FXML
    private GridBoardPane enemyBoard;

    @FXML
    private Label turnLabel;

    private GameFacade facade;

    /**
     * Inicializa el controlador con el motor de juego que administrará la
     * partida. Además de dibujar la flota propia, restaura los marcadores de
     * disparos de una partida cargada, configura los eventos de clic sobre el
     * tablero enemigo y actualiza el indicador de turno.
     *
     * @param engine motor de juego que controlará la partida actual
     */
    public void setGameEngine(GameEngine engine) {
        this.facade = new GameFacade(engine);

        renderOwnFleet();

        restoreShotMarkers();

        setupEnemyBoardClicks();

        refreshTurnLabel();
    }


    /**
     * Dibuja sobre el tablero propio la flota del jugador utilizando las
     * colocaciones suministradas por el modelo.
     */
    private void renderOwnFleet() {
        for (ShipPlacement placement : facade.getPlayerFleet()) {
            GridCoordinate start = new GridCoordinate(placement.startRow(), placement.startColumn());
            ownBoard.placeShipFigure(start, placement.type(), placement.orientation());
        }
    }

    /**
     * Asocia el evento de clic a cada celda del tablero enemigo para permitir
     * que el jugador realice disparos durante su turno.
     */
    private void setupEnemyBoardClicks() {
        for (int row = 0; row < GridBoardPane.BOARD_SIZE; row++) {
            for (int column = 0; column < GridBoardPane.BOARD_SIZE; column++) {
                BoardCell cell = enemyBoard.getCell(row, column);
                cell.setOnMouseClicked(event -> handleEnemyCellClicked(cell));
            }
        }
    }

    /**
     * Procesa el disparo realizado por el jugador sobre una celda del tablero
     * enemigo. Actualiza la representación gráfica del disparo, registra los
     * barcos hundidos para la persistencia, guarda el estado de la partida y
     * continúa el flujo normal del juego.
     *
     * @param cell celda del tablero enemigo seleccionada por el jugador
     */
    private void handleEnemyCellClicked(BoardCell cell) {
        if (facade.isGameOver() || !facade.isPlayerTurn()) {
            return;
        }
        try {
            ShotOutcome outcome = facade.playerShoot(cell.getRow(), cell.getColumn());
            renderShotOutcome(enemyBoard, cell.getRow(), cell.getColumn(), outcome);
            if (outcome.result() == ShotResult.SUNK) {

                PlayerInfoRepository repository = new PlayerInfoRepository();

                int sunk = repository.getShipsSunk();

                // Leer el nickname del archivo
                String nickname = "Jugador";
                try (BufferedReader reader = new BufferedReader(new FileReader("player-info.txt"))) {
                    String line = reader.readLine();
                    if (line != null && line.startsWith("Nickname=")) {
                        nickname = line.substring("Nickname=".length());
                    }
                } catch (IOException ignored) {
                }

                repository.save(nickname, sunk + 1);
            }
            facade.saveGame();
            cell.setDisable(true);
            afterPlayerShot();
        } catch (OutOfBoundsShotException exception) {
            // Defensivo: los clics siempre vienen de una celda válida de la grilla.
        }
    }


    /**
     * Verifica si la partida terminó después del disparo del jugador y, en caso
     * contrario, actualiza el indicador de turno o programa el turno de la IA.
     */
    private void afterPlayerShot() {
        if (facade.isGameOver()) {
            showGameOverAlert();
            return;
        }
        refreshTurnLabel();
        if (!facade.isPlayerTurn()) {
            playAiTurnAfterDelay();
        }
    }

    /**
     * Ejecuta el turno de la inteligencia artificial tras una breve pausa para
     * mejorar la experiencia visual del jugador. Si la IA acierta un disparo,
     * continuará jugando mientras conserve el turno.
     */
    private void playAiTurnAfterDelay() {
        PauseTransition pause = new PauseTransition(AI_TURN_DELAY);
        pause.setOnFinished(event -> {
            AiShotOutcome aiShot = facade.playAiTurn();
            renderShotOutcome(ownBoard, aiShot.row(), aiShot.column(), aiShot.outcome());
            facade.saveGame();

            if (facade.isGameOver()) {
                showGameOverAlert();
                return;
            }

            if (facade.isPlayerTurn()) {
                refreshTurnLabel();
            } else {
                refreshTurnLabel();
                playAiTurnAfterDelay(); // La IA acertó, así que vuelve a disparar
            }
        });
        pause.play();
    }


    /**
     * Dibuja sobre el tablero el resultado de un disparo utilizando el marcador
     * correspondiente (agua, impacto o barco hundido).
     *
     * @param board tablero donde se representará el disparo
     * @param row fila de la casilla impactada
     * @param column columna de la casilla impactada
     * @param outcome resultado del disparo
     */
    private void renderShotOutcome(GridBoardPane board, int row, int column, ShotOutcome outcome) {
        GridCoordinate coordinate = new GridCoordinate(row, column);
        switch (outcome.result()) {
            case WATER -> board.placeMarker(coordinate, new WaterMarkerView(MARKER_SIZE), MARKER_SIZE);
            case HIT -> board.placeMarker(coordinate, new HitMarkerView(MARKER_SIZE), MARKER_SIZE);
            case SUNK -> revealSunkShip(board, outcome.sunkShip());
        }
    }

    /**
     * Restaura los marcadores visuales de ambos tableros cuando se carga una
     * partida previamente guardada, reconstruyendo la representación gráfica de
     * todos los disparos registrados.
     */
    private void restoreShotMarkers() {

        restoreBoard(enemyBoard, facade.getOpponentBoard());
        restoreBoard(ownBoard, facade.getPlayerBoard());

    }

    /**
     * Reconstruye los marcadores de un tablero a partir del estado restaurado por
     * el modelo, representando disparos al agua, impactos y barcos hundidos.
     *
     * @param boardPane tablero gráfico que será actualizado
     * @param board tablero del modelo desde el cual se obtiene el estado
     */
    private void restoreBoard(GridBoardPane boardPane, Board board) {

        Set<Ship> revealedShips = new HashSet<>();

        for (Coordinate coordinate : board.getFiredShots()) {

            Ship ship = board.shipAt(coordinate);
            if (ship == null) {

                boardPane.placeMarker(
                        new GridCoordinate(coordinate.row(), coordinate.column()),
                        new WaterMarkerView(MARKER_SIZE),
                        MARKER_SIZE
                );

                boardPane.getCell(coordinate.row(), coordinate.column())
                        .setDisable(true);

            }
            else if (ship.isSunk()) {

                if (revealedShips.add(ship)) {
                    revealSunkShip(boardPane, ship.getPlacement());
                }

            }
            else {

                boardPane.placeMarker(
                        new GridCoordinate(coordinate.row(), coordinate.column()),
                        new HitMarkerView(MARKER_SIZE),
                        MARKER_SIZE
                );

                boardPane.getCell(coordinate.row(), coordinate.column())
                        .setDisable(true);

            }


        }
    }

    /**
     * Revela gráficamente un barco hundido dibujando su silueta y los marcadores
     * correspondientes sobre todas las casillas que ocupa.
     *
     * @param board tablero donde se mostrará el barco hundido
     * @param sunkShip colocación del barco que fue hundido
     */
    private void revealSunkShip(GridBoardPane board, ShipPlacement sunkShip) {
        ShipView shipView = ShipViewFactory.createShipView(sunkShip.type(), BoardCell.CELL_SIZE, sunkShip.orientation());
        shipView.markSunk();
        GridCoordinate start = new GridCoordinate(sunkShip.startRow(), sunkShip.startColumn());
        board.placeShipFigure(start, shipView);

        int size = sunkShip.type().getSizeInCells();
        boolean horizontal = sunkShip.orientation() == Orientation.HORIZONTAL;
        for (int offset = 0; offset < size; offset++) {
            int row = horizontal ? sunkShip.startRow() : sunkShip.startRow() + offset;
            int column = horizontal ? sunkShip.startColumn() + offset : sunkShip.startColumn();
            GridCoordinate cellCoordinate = new GridCoordinate(row, column);
            board.placeMarker(cellCoordinate, new SunkMarkerView(MARKER_SIZE), MARKER_SIZE);
            board.getCell(row, column).setDisable(true);
        }
    }

    /**
     * Actualiza el mensaje que indica de quién es el turno actual.
     */
    private void refreshTurnLabel() {
        turnLabel.setText(facade.isPlayerTurn() ? "Tu turno" : "Turno de la IA…");
    }


    /**
     * Muestra un mensaje indicando el resultado final de la partida y actualiza
     * el indicador de turno con el estado de victoria o derrota.
     */
    private void showGameOverAlert() {
        boolean playerWon = facade.didPlayerWin();
        Alert alert = new Alert(playerWon ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                playerWon ? "¡Hundiste toda la flota enemiga! Ganaste." : "La IA hundió toda tu flota. Perdiste.");
        alert.setHeaderText(null);
        alert.showAndWait();
        turnLabel.setText(playerWon ? "¡Ganaste!" : "Perdiste");
    }
}
