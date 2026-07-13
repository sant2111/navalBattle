package com.example.navalbattle.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * Marca de "tocado" (disparo que impactó un barco): una bomba estilizada
 * (cuerpo, mecha y chispa), hecha solo con {@code javafx.scene.shape} (sin
 * imágenes externas).
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): esta misma figura y
 * color se reutilizarán en el tablero de juego para marcar cualquier
 * celda tocada.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class HitMarkIcon extends Group {

    private static final Color BODY_COLOR = Color.web("#D64550");
    private static final Color SPARK_COLOR = Color.web("#E8871E");

    /**
     * Construye el ícono y añade sus figuras como hijas de este {@link Group}.
     */
    public HitMarkIcon() {
        Circle body = new Circle(8, 10, 6);
        body.setFill(BODY_COLOR);

        Line fuse = new Line(11, 6, 13, 3);
        fuse.setStroke(BODY_COLOR.darker());
        fuse.setStrokeWidth(1.6);

        Polygon spark = new Polygon(
                13, 0,
                16, 2,
                13, 4,
                11, 2
        );
        spark.setFill(SPARK_COLOR);

        getChildren().addAll(body, fuse, spark);
    }
}
