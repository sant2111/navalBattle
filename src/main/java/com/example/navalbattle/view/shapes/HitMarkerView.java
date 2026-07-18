package com.example.navalbattle.view.shapes;

import com.example.navalbattle.view.GameColors;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

/**
 * Marca de "tocado" (disparo que impactó un barco): un círculo rojo con
 * un núcleo amarillo, hecha solo con {@code javafx.scene.shape} (sin
 * imágenes externas).
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): misma figura y
 * color en cualquier tablero del juego, y en los impactos que
 * {@link ShipView#markHit(int)} dibuja sobre el propio barco.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class HitMarkerView extends Group {

    private static final double OUTER_RADIUS_FACTOR = 0.4;
    private static final double INNER_RADIUS_FACTOR = 0.2;

    /**
     * @param size lado del área cuadrada donde se dibuja la marca
     */
    public HitMarkerView(double size) {
        double centerX = size / 2;
        double centerY = size / 2;

        Circle outer = new Circle(centerX, centerY, size * OUTER_RADIUS_FACTOR);
        outer.setFill(GameColors.HIT_MARK_OUTER);
        outer.setStroke(GameColors.HULL_BORDER);
        outer.setStrokeWidth(1);

        Circle inner = new Circle(centerX, centerY, size * INNER_RADIUS_FACTOR);
        inner.setFill(GameColors.HIT_MARK_INNER);

        getChildren().addAll(outer, inner);
    }
}
