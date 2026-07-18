package com.example.navalbattle.view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Una celda cuadrada individual de {@link GridBoardPane}: agua, o
 * resaltada temporalmente como zona de arrastre válida/inválida.
 * Dibujada con {@code javafx.scene.shape.Rectangle} (sin imágenes
 * externas), con el color aplicado directamente en Java para garantizar
 * que se renderice de forma confiable.
 * <p>
 * Las siluetas de los barcos colocados NO se dibujan aquí: se dibujan
 * como una sola figura continua en la capa de superposición de
 * {@link GridBoardPane} (ver {@code view.shapes.ShipView}), para que un barco de
 * varias casillas se vea como un barco y no como celdas sueltas más
 * oscuras.
 * <p>
 * Heurística 5 de Nielsen (prevención de errores): el resaltado verde o
 * rojo durante el arrastre le muestra al jugador, antes de soltar, si esa
 * colocación es válida.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class BoardCell extends StackPane {

    /** Tamaño en píxeles de cada celda cuadrada. */
    public static final double CELL_SIZE = 34;

    private static final Color EMPTY_FILL = Color.web("#C9D6DF");
    private static final Color EMPTY_STROKE = Color.web("#9FB2BE");
    private static final Color VALID_HOVER_FILL = Color.web("#4CAF50");
    private static final Color VALID_HOVER_STROKE = Color.web("#2E7D32");
    private static final Color INVALID_HOVER_FILL = Color.web("#D64550");
    private static final Color INVALID_HOVER_STROKE = Color.web("#8E2A32");

    private final Rectangle background;
    private final int row;
    private final int column;

    /**
     * Construye una celda de agua en la posición dada.
     *
     * @param row    fila de la celda, de 0 a 9
     * @param column columna de la celda, de 0 a 9
     */
    public BoardCell(int row, int column) {
        this.row = row;
        this.column = column;
        background = new Rectangle(CELL_SIZE, CELL_SIZE);
        background.setStrokeWidth(0.8);
        background.setFill(EMPTY_FILL);
        background.setStroke(EMPTY_STROKE);
        getChildren().add(background);
    }

    /** @return fila de esta celda, de 0 a 9 */
    public int getRow() {
        return row;
    }

    /** @return columna de esta celda, de 0 a 9 */
    public int getColumn() {
        return column;
    }

    /**
     * Muestra temporalmente un resaltado de arrastre.
     *
     * @param valid {@code true} para resaltado verde (colocación válida),
     *              {@code false} para resaltado rojo (inválida)
     */
    public void showHover(boolean valid) {
        background.setFill(valid ? VALID_HOVER_FILL : INVALID_HOVER_FILL);
        background.setStroke(valid ? VALID_HOVER_STROKE : INVALID_HOVER_STROKE);
    }

    /** Quita el resaltado de arrastre y vuelve a mostrar agua vacía. */
    public void clearHover() {
        background.setFill(EMPTY_FILL);
        background.setStroke(EMPTY_STROKE);
    }
}
