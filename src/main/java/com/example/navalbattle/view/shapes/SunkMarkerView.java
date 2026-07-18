package com.example.navalbattle.view.shapes;

import com.example.navalbattle.view.GameColors;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/**
 * Marca de "hundido" (barco completamente destruido): llamas de dos
 * tonos con humo saliendo por encima, hecha solo con
 * {@code javafx.scene.shape} (sin imágenes externas). Deliberadamente
 * más compleja que {@link HitMarkerView} para que se distinga de un
 * vistazo que ese barco ya está fuera de juego.
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): misma figura y
 * color en cualquier tablero del juego.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class SunkMarkerView extends Group {

    /**
     * @param size lado del área cuadrada donde se dibuja la marca
     */
    public SunkMarkerView(double size) {
        Polygon outerFlame = new Polygon(
                size * 0.5, size * 0.05,
                size * 0.75, size * 0.4,
                size * 0.85, size * 0.65,
                size * 0.62, size * 1.0,
                size * 0.38, size * 1.0,
                size * 0.15, size * 0.65,
                size * 0.28, size * 0.4
        );
        outerFlame.setFill(GameColors.SUNK_FLAME_RED);

        Polygon innerFlame = new Polygon(
                size * 0.5, size * 0.3,
                size * 0.65, size * 0.55,
                size * 0.58, size * 0.85,
                size * 0.42, size * 0.85,
                size * 0.35, size * 0.55
        );
        innerFlame.setFill(GameColors.SUNK_FLAME_ORANGE);

        Circle smokeLeft = new Circle(size * 0.28, size * 0.12, size * 0.13);
        smokeLeft.setFill(GameColors.SUNK_SMOKE_GRAY);
        smokeLeft.setOpacity(0.55);

        Circle smokeRight = new Circle(size * 0.62, size * 0.02, size * 0.1);
        smokeRight.setFill(GameColors.SUNK_SMOKE_GRAY);
        smokeRight.setOpacity(0.4);

        getChildren().addAll(outerFlame, innerFlame, smokeLeft, smokeRight);
    }
}
