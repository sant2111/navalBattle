package com.example.navalbattle.view.shapes;

import com.example.navalbattle.view.GameColors;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * Marca de "agua" (disparo fallido): una X dibujada con dos líneas
 * cruzadas, hecha solo con {@code javafx.scene.shape} (sin imágenes
 * externas).
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): misma figura y
 * color en cualquier tablero del juego. Deliberadamente en un tono
 * neutro ({@link GameColors#WATER_MARK}), distinto del rojo de
 * {@link HitMarkerView}, para que ambas marcas no se confundan de un
 * vistazo.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class WaterMarkerView extends Group {

    private static final double STROKE_WIDTH_FACTOR = 0.14;
    private static final double MARGIN_FACTOR = 0.2;

    /**
     * @param size lado del área cuadrada donde se dibuja la marca
     */
    public WaterMarkerView(double size) {
        double margin = size * MARGIN_FACTOR;
        double strokeWidth = size * STROKE_WIDTH_FACTOR;

        Line diagonalDown = new Line(margin, margin, size - margin, size - margin);
        Line diagonalUp = new Line(size - margin, margin, margin, size - margin);

        for (Line line : new Line[]{diagonalDown, diagonalUp}) {
            line.setStroke(GameColors.WATER_MARK);
            line.setStrokeWidth(strokeWidth);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
        }

        getChildren().addAll(diagonalDown, diagonalUp);
    }
}
