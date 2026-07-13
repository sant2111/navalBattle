package com.example.navalbattle.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Marca de "hundido" (barco completamente destruido): una llama
 * estilizada de dos tonos, hecha solo con {@code javafx.scene.shape} (sin
 * imágenes externas).
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): esta misma figura y
 * color se reutilizarán en el tablero de juego para marcar cualquier
 * barco hundido.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class SunkMarkIcon extends Group {

    private static final Color OUTER_COLOR = Color.web("#E8871E");
    private static final Color INNER_COLOR = Color.web("#F6C453");

    /**
     * Construye el ícono y añade sus figuras como hijas de este {@link Group}.
     */
    public SunkMarkIcon() {
        Polygon outerFlame = new Polygon(
                8, 0,
                12, 5,
                14, 9,
                11, 16,
                5, 16,
                2, 9,
                4, 5
        );
        outerFlame.setFill(OUTER_COLOR);

        Polygon innerFlame = new Polygon(
                8, 5,
                10, 8,
                9, 13,
                7, 13,
                6, 8
        );
        innerFlame.setFill(INNER_COLOR);

        getChildren().addAll(outerFlame, innerFlame);
    }
}
