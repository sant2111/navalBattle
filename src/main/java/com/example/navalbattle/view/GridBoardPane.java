package com.example.navalbattle.view;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.shapes.ShipView;
import com.example.navalbattle.view.shapes.ShipViewFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.util.HashMap;
import java.util.Map;

/**
 * Grilla reutilizable de 10x10 con encabezados de coordenadas siempre
 * visibles (columnas A-J, filas 1-10), usada en la colocación de la flota
 * y, más adelante, en el tablero de disparo.
 * <p>
 * Combina dos capas EXACTAMENTE alineadas entre sí: la grilla de celdas
 * de agua ({@link BoardCell}), con cada fila y columna (incluidos los
 * encabezados) fijada a {@link BoardCell#CELL_SIZE} mediante
 * {@link ColumnConstraints}/{@link RowConstraints}; y una capa
 * transparente superpuesta donde se dibujan las siluetas de los barcos
 * ya colocados, para que un barco de varias casillas se vea como una
 * sola figura continua y no como celdas sueltas más oscuras.
 * <p>
 * Heurística 6 de Nielsen (reconocimiento en lugar de recuerdo): las
 * coordenadas quedan siempre a la vista, el jugador no debe contar
 * celdas de memoria.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class GridBoardPane extends Pane {

    /** Cantidad de filas y columnas del tablero (10x10). */
    public static final int BOARD_SIZE = 10;

    private static final String COLUMN_LETTERS = "ABCDEFGHIJ";
    private static final double HEADER_SIZE = BoardCell.CELL_SIZE;

    private final BoardCell[][] cells = new BoardCell[BOARD_SIZE][BOARD_SIZE];
    private final Pane shipOverlay = new Pane();
    private final Map<GridCoordinate, Node> placedShipFigures = new HashMap<>();

    /**
     * Construye la grilla completa: encabezados de coordenadas, las 100
     * celdas de agua y la capa de superposición para las siluetas de
     * los barcos.
     */
    public GridBoardPane() {
        getStyleClass().add("grid-board");

        GridPane grid = new GridPane();
        configureFixedSizeConstraints(grid);
        buildHeaders(grid);
        buildCells(grid);

        shipOverlay.setLayoutX(HEADER_SIZE);
        shipOverlay.setLayoutY(HEADER_SIZE);
        shipOverlay.setMouseTransparent(true);

        getChildren().addAll(grid, shipOverlay);
    }

    /**
     * Fija cada fila y columna (encabezado incluido) exactamente a
     * {@link BoardCell#CELL_SIZE}. Sin esto, {@code GridPane} calcula el
     * ancho de la columna/fila de encabezados según el tamaño del texto
     * ("1".."10"), que es más angosto que una celda, y la capa de
     * siluetas de barcos queda desalineada respecto a la grilla real.
     */
    private void configureFixedSizeConstraints(GridPane grid) {
        ColumnConstraints headerColumn = new ColumnConstraints(HEADER_SIZE);
        grid.getColumnConstraints().add(headerColumn);
        for (int column = 0; column < BOARD_SIZE; column++) {
            grid.getColumnConstraints().add(new ColumnConstraints(BoardCell.CELL_SIZE));
        }

        RowConstraints headerRow = new RowConstraints(HEADER_SIZE);
        grid.getRowConstraints().add(headerRow);
        for (int row = 0; row < BOARD_SIZE; row++) {
            grid.getRowConstraints().add(new RowConstraints(BoardCell.CELL_SIZE));
        }
    }

    private void buildHeaders(GridPane grid) {
        grid.add(new Label(), 0, 0);

        for (int column = 0; column < BOARD_SIZE; column++) {
            Label columnHeader = new Label(String.valueOf(COLUMN_LETTERS.charAt(column)));
            columnHeader.getStyleClass().add("grid-header");
            columnHeader.setAlignment(Pos.CENTER);
            columnHeader.setMaxWidth(Double.MAX_VALUE);
            grid.add(columnHeader, column + 1, 0);
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            Label rowHeader = new Label(String.valueOf(row + 1));
            rowHeader.getStyleClass().add("grid-header");
            rowHeader.setAlignment(Pos.CENTER);
            rowHeader.setMaxWidth(Double.MAX_VALUE);
            grid.add(rowHeader, 0, row + 1);
        }
    }

    private void buildCells(GridPane grid) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                BoardCell cell = new BoardCell(row, column);
                cells[row][column] = cell;
                grid.add(cell, column + 1, row + 1);
            }
        }
    }

    /**
     * @param row    fila de la celda buscada, de 0 a 9
     * @param column columna de la celda buscada, de 0 a 9
     * @return la celda en esa posición
     */
    public BoardCell getCell(int row, int column) {
        return cells[row][column];
    }

    /**
     * Dibuja la silueta de un barco sobre la capa de superposición,
     * comenzando en la coordenada dada.
     *
     * @param start       coordenada donde inicia el barco
     * @param type        tipo de barco (define tamaño y silueta)
     * @param orientation orientación del barco
     */
    public void placeShipFigure(GridCoordinate start, ShipType type, Orientation orientation) {
        ShipView figure = ShipViewFactory.createShipView(type, BoardCell.CELL_SIZE, orientation);
        figure.setLayoutX(start.column() * BoardCell.CELL_SIZE + figure.getGridOffsetX());
        figure.setLayoutY(start.row() * BoardCell.CELL_SIZE + figure.getGridOffsetY());
        shipOverlay.getChildren().add(figure);
        placedShipFigures.put(start, figure);
    }

    /**
     * Quita la silueta de un barco que iniciaba en la coordenada dada.
     *
     * @param start coordenada donde iniciaba el barco a quitar
     */
    public void clearShipFigure(GridCoordinate start) {
        Node figure = placedShipFigures.remove(start);
        if (figure != null) {
            shipOverlay.getChildren().remove(figure);
        }
    }

    /** Quita todas las siluetas de barcos dibujadas sobre la grilla. */
    public void clearAllShipFigures() {
        shipOverlay.getChildren().clear();
        placedShipFigures.clear();
    }
}
