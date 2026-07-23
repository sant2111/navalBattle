package com.example.navalbattle.controller;

import com.example.navalbattle.exceptions.OutOfBoundsShotException;
import com.example.navalbattle.model.AiShotOutcome;
import com.example.navalbattle.model.GameEngine;
import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipPlacement;
import com.example.navalbattle.model.ShotOutcome;
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
     * Punto de inyección del motor de partida: quien abra esta vista
     * debe llamar a este método antes de mostrarla.
     * <p>
     * ⚠️ IMPACTO AL EQUIPO: este es el contrato ({@link GameEngine}) que
     * debe cumplir el motor real de modelo/IA para reemplazar a
     * {@code MockGameEngine}.
     *
     * @param engine motor de partida (real o de prueba)
     */
    public void setGameEngine(GameEngine engine) {
        this.facade = new GameFacade(engine);
        renderOwnFleet();
        setupEnemyBoardClicks();
        refreshTurnLabel();
    }

    private void renderOwnFleet() {
        for (ShipPlacement placement : facade.getPlayerFleet()) {
            GridCoordinate start = new GridCoordinate(placement.startRow(), placement.startColumn());
            ownBoard.placeShipFigure(start, placement.type(), placement.orientation());
        }
    }

    private void setupEnemyBoardClicks() {
        for (int row = 0; row < GridBoardPane.BOARD_SIZE; row++) {
            for (int column = 0; column < GridBoardPane.BOARD_SIZE; column++) {
                BoardCell cell = enemyBoard.getCell(row, column);
                cell.setOnMouseClicked(event -> handleEnemyCellClicked(cell));
            }
        }
    }

    private void handleEnemyCellClicked(BoardCell cell) {
        if (facade.isGameOver() || !facade.isPlayerTurn()) {
            return;
        }
        try {
            ShotOutcome outcome = facade.playerShoot(cell.getRow(), cell.getColumn());
            renderShotOutcome(enemyBoard, cell.getRow(), cell.getColumn(), outcome);
            facade.saveGame();
            cell.setDisable(true);
            afterPlayerShot();
        } catch (OutOfBoundsShotException exception) {
            // Defensivo: los clics siempre vienen de una celda válida de la grilla.
        }
    }

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

    private void renderShotOutcome(GridBoardPane board, int row, int column, ShotOutcome outcome) {
        GridCoordinate coordinate = new GridCoordinate(row, column);
        switch (outcome.result()) {
            case WATER -> board.placeMarker(coordinate, new WaterMarkerView(MARKER_SIZE), MARKER_SIZE);
            case HIT -> board.placeMarker(coordinate, new HitMarkerView(MARKER_SIZE), MARKER_SIZE);
            case SUNK -> revealSunkShip(board, outcome.sunkShip());
        }
    }

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

    private void refreshTurnLabel() {
        turnLabel.setText(facade.isPlayerTurn() ? "Tu turno" : "Turno de la IA…");
    }

    private void showGameOverAlert() {
        boolean playerWon = facade.didPlayerWin();
        Alert alert = new Alert(playerWon ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                playerWon ? "¡Hundiste toda la flota enemiga! Ganaste." : "La IA hundió toda tu flota. Perdiste.");
        alert.setHeaderText(null);
        alert.showAndWait();
        turnLabel.setText(playerWon ? "¡Ganaste!" : "Perdiste");
    }
}
