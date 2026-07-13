package com.example.navalbattle.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * Marca de "agua" (disparo fallido): una X dibujada con dos líneas
 * cruzadas, hecha solo con {@code javafx.scene.shape} (sin imágenes
 * externas).
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): esta misma figura y
 * color se reutilizarán en el tablero de juego para marcar cualquier
 * celda de agua, y aquí en la leyenda de bienvenida se enseñan primero
 * (heurística 6: reconocimiento en lugar de recuerdo).
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class WaterMarkIcon extends Group {

    private static final double SIZE = 16;
    private static final double MARGIN = 2;
    private static final Color MARK_COLOR = Color.web("#5C6B73");
    private static final double STROKE_WIDTH = 2.4;

    /**
     * Construye el ícono y añade sus figuras como hijas de este {@link Group}.
     */
    public WaterMarkIcon() {
        Line diagonalDown = new Line(MARGIN, MARGIN, SIZE - MARGIN, SIZE - MARGIN);
        Line diagonalUp = new Line(SIZE - MARGIN, MARGIN, MARGIN, SIZE - MARGIN);

        diagonalDown.setStroke(MARK_COLOR);
        diagonalDown.setStrokeWidth(STROKE_WIDTH);
        diagonalDown.setStrokeLineCap(StrokeLineCap.ROUND);

        diagonalUp.setStroke(MARK_COLOR);
        diagonalUp.setStrokeWidth(STROKE_WIDTH);
        diagonalUp.setStrokeLineCap(StrokeLineCap.ROUND);

        getChildren().addAll(diagonalDown, diagonalUp);
    }
}
