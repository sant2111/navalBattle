package com.example.navalbattle.controller;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.BoardCell;
import com.example.navalbattle.view.GameStage;
import com.example.navalbattle.view.GridBoardPane;
import com.example.navalbattle.view.GridCoordinate;
import com.example.navalbattle.view.shapes.ShipViewFactory;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controlador de {@code board-setup-view.fxml}: coloca la flota fija del
 * juego (1 portaaviones, 2 submarinos, 3 destructores, 4 fragatas) sobre
 * una grilla de 10x10 mediante arrastrar y soltar.
 * <p>
 * Un barco ya colocado no queda fijo: se puede volver a arrastrar desde
 * la grilla para reubicarlo, o hacer doble clic sobre él para rotarlo en
 * su mismo sitio, sin pasar por "Deshacer".
 * <p>
 * Heurísticas de Nielsen aplicadas:
 * <ul>
 *   <li>1 — Visibilidad del estado: contador de barcos pendientes y
 *       "Comenzar partida" solo se habilita con la flota completa.</li>
 *   <li>4 — Consistencia: mismas siluetas y colores de casco en el panel
 *       de flota y en la grilla.</li>
 *   <li>5 — Prevención de errores: cada celda sobre la que se arrastra un
 *       barco se resalta en verde (colocación válida) o rojo (inválida:
 *       fuera del tablero o solapada con otro barco) antes de soltar; si
 *       se suelta en un lugar inválido, el barco vuelve a su posición
 *       anterior en vez de perderse.</li>
 *   <li>9 — Recuperación de errores: "Deshacer" revierte la última
 *       colocación sin penalizar al jugador por un error de arrastre.</li>
 * </ul>
 * <p>
 * Estructuras de datos: se usa una {@link List} para la flota pendiente
 * de colocar (fuente de verdad de cuántos barcos de cada tipo quedan) y
 * una {@link Deque} como Pila para "Deshacer", porque deshacer siempre
 * debe revertir la última colocación (semántica LIFO), nunca una
 * arbitraria. Un {@link Map} de coordenada a colocación es la fuente de
 * verdad de qué celdas están ocupadas y por cuál barco. La orientación se
 * guarda por tipo de barco (no una sola orientación global) para que
 * rotar un barco pendiente no cambie los demás.
 * <p>
 * Eventos escuchados: arrastre (drag detected/over/dropped/done) entre el
 * panel de flota y la grilla, y desde la grilla hacia sí misma para
 * reubicar un barco ya puesto; doble clic sobre una celda para rotar el
 * barco que la ocupa; clic en el botón de rotar de cada barco pendiente y
 * en los botones de colocación aleatoria, deshacer, reiniciar, volver y
 * comenzar partida.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class BoardSetupController {

    private static final int BOARD_SIZE = GridBoardPane.BOARD_SIZE;
    private static final int RANDOM_PLACEMENT_MAX_ATTEMPTS = 200;

    @FXML
    private GridBoardPane gridBoardPane;

    @FXML
    private VBox fleetListContainer;

    @FXML
    private Label remainingShipsLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button startGameButton;

    private final List<ShipType> pendingShips = new ArrayList<>();
    private final Deque<ShipPlacement> placementHistory = new ArrayDeque<>();
    private final Map<GridCoordinate, ShipPlacement> placementByCell = new HashMap<>();
    private final Map<ShipType, Orientation> orientationByType = new EnumMap<>(ShipType.class);
    private final List<BoardCell> highlightedCells = new ArrayList<>();
    private final Random random = new Random();

    private ShipType shipBeingDragged;
    private Orientation orientationBeingDragged;
    private ShipPlacement placementBeingMoved;
    private Stage previousStage;

    /**
     * Punto de inyección de la ventana anterior, para que "Volver" pueda
     * reabrirla en lugar de simplemente cerrar la aplicación.
     *
     * @param previousStage ventana que debe reaparecer al volver
     */
    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    @FXML
    private void initialize() {
        for (ShipType type : ShipType.values()) {
            orientationByType.put(type, Orientation.HORIZONTAL);
        }
        fillPendingShipsFromFleet();
        setupGridHandlers();
        refreshFleetPanel();
        refreshRemainingLabel();
    }

    private void fillPendingShipsFromFleet() {
        for (ShipType type : ShipType.values()) {
            for (int unit = 0; unit < type.getFleetCount(); unit++) {
                pendingShips.add(type);
            }
        }
    }

    private void setupGridHandlers() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                BoardCell cell = gridBoardPane.getCell(row, column);
                cell.setOnDragDetected(this::handleCellDragDetected);
                cell.setOnDragOver(this::handleCellDragOver);
                cell.setOnDragDropped(this::handleCellDragDropped);
                cell.setOnDragDone(this::handleDragDone);
                cell.setOnMouseClicked(this::handleCellClicked);
            }
        }
    }

    /** Empieza a arrastrar un barco YA colocado, tomándolo de la grilla para reubicarlo. */
    private void handleCellDragDetected(MouseEvent event) {
        BoardCell cell = (BoardCell) event.getSource();
        ShipPlacement placement = placementByCell.get(new GridCoordinate(cell.getRow(), cell.getColumn()));
        if (placement == null) {
            return;
        }

        placementBeingMoved = placement;
        shipBeingDragged = placement.getType();
        orientationBeingDragged = placement.getOrientation();
        unregisterPlacement(placement);
        placementHistory.remove(placement);
        undoButton.setDisable(placementHistory.isEmpty());

        Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(placement.getType().name());
        dragboard.setContent(content);
        event.consume();
    }

    private void handleCellDragOver(DragEvent event) {
        if (shipBeingDragged != null && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
            BoardCell cell = (BoardCell) event.getSource();
            highlightFootprint(cell.getRow(), cell.getColumn(), shipBeingDragged, orientationBeingDragged);
        }
        event.consume();
    }

    private void handleCellDragDropped(DragEvent event) {
        BoardCell cell = (BoardCell) event.getSource();
        boolean success = false;
        if (shipBeingDragged != null) {
            List<GridCoordinate> footprint = ShipPlacement.computeCells(
                    shipBeingDragged, cell.getRow(), cell.getColumn(), orientationBeingDragged);
            if (isFootprintValid(footprint)) {
                ShipPlacement newPlacement =
                        new ShipPlacement(shipBeingDragged, cell.getRow(), cell.getColumn(), orientationBeingDragged);
                registerPlacement(newPlacement);
                placementHistory.push(newPlacement);
                if (placementBeingMoved == null) {
                    pendingShips.remove(shipBeingDragged);
                }

                refreshFleetPanel();
                refreshRemainingLabel();
                undoButton.setDisable(false);
                startGameButton.setDisable(!pendingShips.isEmpty());
                success = true;
            }
        }
        event.setDropCompleted(success);
        clearHighlights();
        event.consume();
    }

    /**
     * Se dispara al terminar CUALQUIER arrastre (desde el panel de flota
     * o desde la grilla). Si el arrastre venía de mover un barco ya
     * colocado y no se soltó en un lugar válido, lo devuelve a su
     * posición original (heurística 5: nada se pierde por un arrastre
     * fallido).
     */
    private void handleDragDone(DragEvent event) {
        if (event.getTransferMode() == null && placementBeingMoved != null) {
            registerPlacement(placementBeingMoved);
            placementHistory.push(placementBeingMoved);
            undoButton.setDisable(placementHistory.isEmpty());
        }
        shipBeingDragged = null;
        orientationBeingDragged = null;
        placementBeingMoved = null;
        clearHighlights();
        event.consume();
    }

    /** Doble clic sobre un barco ya colocado lo rota 90° en su mismo sitio, si cabe. */
    private void handleCellClicked(MouseEvent event) {
        if (event.getClickCount() != 2) {
            return;
        }
        BoardCell cell = (BoardCell) event.getSource();
        ShipPlacement placement = placementByCell.get(new GridCoordinate(cell.getRow(), cell.getColumn()));
        if (placement == null) {
            return;
        }

        Orientation rotated = placement.getOrientation() == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
        GridCoordinate start = placement.getStart();
        List<GridCoordinate> rotatedFootprint =
                ShipPlacement.computeCells(placement.getType(), start.row(), start.column(), rotated);

        unregisterPlacement(placement);
        if (isFootprintValid(rotatedFootprint)) {
            ShipPlacement rotatedPlacement = new ShipPlacement(placement.getType(), start.row(), start.column(), rotated);
            registerPlacement(rotatedPlacement);
            placementHistory.remove(placement);
            placementHistory.push(rotatedPlacement);
        } else {
            registerPlacement(placement);
        }
    }

    private void highlightFootprint(int startRow, int startColumn, ShipType type, Orientation orientation) {
        clearHighlights();
        List<GridCoordinate> footprint = ShipPlacement.computeCells(type, startRow, startColumn, orientation);
        boolean valid = isFootprintValid(footprint);
        for (GridCoordinate coordinate : footprint) {
            if (isInBounds(coordinate)) {
                BoardCell cell = gridBoardPane.getCell(coordinate.row(), coordinate.column());
                cell.showHover(valid);
                highlightedCells.add(cell);
            }
        }
    }

    private void clearHighlights() {
        for (BoardCell cell : highlightedCells) {
            cell.clearHover();
        }
        highlightedCells.clear();
    }

    private boolean isFootprintValid(List<GridCoordinate> footprint) {
        for (GridCoordinate coordinate : footprint) {
            if (!isInBounds(coordinate) || placementByCell.containsKey(coordinate)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInBounds(GridCoordinate coordinate) {
        return coordinate.row() >= 0 && coordinate.row() < BOARD_SIZE
                && coordinate.column() >= 0 && coordinate.column() < BOARD_SIZE;
    }

    private void registerPlacement(ShipPlacement placement) {
        for (GridCoordinate coordinate : placement.occupiedCells()) {
            placementByCell.put(coordinate, placement);
        }
        gridBoardPane.placeShipFigure(placement.getStart(), placement.getType(), placement.getOrientation());
    }

    private void unregisterPlacement(ShipPlacement placement) {
        for (GridCoordinate coordinate : placement.occupiedCells()) {
            placementByCell.remove(coordinate);
        }
        gridBoardPane.clearShipFigure(placement.getStart());
    }

    @FXML
    private void handleUndoAction() {
        if (placementHistory.isEmpty()) {
            return;
        }
        ShipPlacement lastPlacement = placementHistory.pop();
        unregisterPlacement(lastPlacement);
        pendingShips.add(lastPlacement.getType());

        refreshFleetPanel();
        refreshRemainingLabel();
        undoButton.setDisable(placementHistory.isEmpty());
        startGameButton.setDisable(true);
    }

    @FXML
    private void handleResetAction() {
        gridBoardPane.clearAllShipFigures();
        placementByCell.clear();
        pendingShips.clear();
        placementHistory.clear();
        fillPendingShipsFromFleet();

        refreshFleetPanel();
        refreshRemainingLabel();
        undoButton.setDisable(true);
        startGameButton.setDisable(true);
    }

    @FXML
    private void handleRandomPlacementAction() {
        List<ShipType> shipsToPlace = new ArrayList<>(pendingShips);
        for (ShipType type : shipsToPlace) {
            placeShipRandomly(type);
        }
        refreshFleetPanel();
        refreshRemainingLabel();
        undoButton.setDisable(placementHistory.isEmpty());
        startGameButton.setDisable(!pendingShips.isEmpty());
    }

    private void placeShipRandomly(ShipType type) {
        for (int attempt = 0; attempt < RANDOM_PLACEMENT_MAX_ATTEMPTS; attempt++) {
            Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            int startRow = random.nextInt(BOARD_SIZE);
            int startColumn = random.nextInt(BOARD_SIZE);
            List<GridCoordinate> footprint = ShipPlacement.computeCells(type, startRow, startColumn, orientation);
            if (isFootprintValid(footprint)) {
                ShipPlacement placement = new ShipPlacement(type, startRow, startColumn, orientation);
                registerPlacement(placement);
                pendingShips.remove(type);
                placementHistory.push(placement);
                return;
            }
        }
    }

    private void refreshFleetPanel() {
        fleetListContainer.getChildren().clear();
        for (ShipType type : ShipType.values()) {
            int remaining = Collections.frequency(pendingShips, type);
            if (remaining > 0) {
                fleetListContainer.getChildren().add(buildFleetRow(type, remaining));
            }
        }
    }

    /**
     * Construye una fila del panel de flota para un tipo de barco: su
     * silueta, la cantidad restante y un botón de rotar que solo cambia
     * la orientación de ESE tipo de barco (no de toda la interfaz).
     */
    private HBox buildFleetRow(ShipType type, int remaining) {
        StackPane previewHolder = new StackPane(
                ShipViewFactory.createShipView(type, BoardCell.CELL_SIZE, orientationByType.get(type)));

        Label label = new Label(type.getDisplayName() + " ×" + remaining);
        label.getStyleClass().add("fleet-row-label");

        Button rotateShipButton = new Button("⟳");
        rotateShipButton.getStyleClass().add("rotate-icon-button");
        rotateShipButton.setOnAction(event -> {
            Orientation rotated = orientationByType.get(type) == Orientation.HORIZONTAL
                    ? Orientation.VERTICAL
                    : Orientation.HORIZONTAL;
            orientationByType.put(type, rotated);
            previewHolder.getChildren().setAll(ShipViewFactory.createShipView(type, BoardCell.CELL_SIZE, rotated));
        });

        HBox row = new HBox(10, previewHolder, label, rotateShipButton);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("fleet-row");
        row.setOnDragDetected(event -> {
            shipBeingDragged = type;
            orientationBeingDragged = orientationByType.get(type);
            placementBeingMoved = null;
            Dragboard dragboard = row.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(type.name());
            dragboard.setContent(content);
            event.consume();
        });
        row.setOnDragDone(this::handleDragDone);
        return row;
    }

    private void refreshRemainingLabel() {
        int totalRemaining = pendingShips.size();
        remainingShipsLabel.setText(totalRemaining == 0
                ? "¡Flota lista!"
                : totalRemaining + " barco(s) por colocar");
    }

    @FXML
    private void handleBackAction() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();
        if (previousStage != null) {
            previousStage.show();
        }
    }

    /**
     * Maneja el clic en "Comenzar partida": abre {@link GameStage}.
     * <p>
     * ⚠️ IMPACTO AL EQUIPO: por ahora {@link GameStage} solo muestra el
     * layout del tablero de combate; falta conectar la flota realmente
     * colocada y la lógica de turnos/disparos una vez exista el motor
     * real de modelo/IA.
     */
    @FXML
    private void handleStartGameAction() {
        try {
            GameStage gameStage = new GameStage();
            gameStage.show();
            ((Stage) startGameButton.getScene().getWindow()).close();
        } catch (IOException exception) {
            showErrorAlert("No se pudo abrir el tablero de combate.");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
