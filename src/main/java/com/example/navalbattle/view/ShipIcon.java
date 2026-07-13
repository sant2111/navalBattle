package com.example.navalbattle.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * Ícono decorativo de un barco simplificado, compuesto únicamente por
 * figuras 2D de {@code javafx.scene.shape} (mástil, bandera, casco y olas):
 * ninguna imagen externa. Se usa como acento visual reutilizable en la
 * cabecera de las vistas del juego.
 * <p>
 * Heurística 8 de Nielsen (diseño estético y minimalista): refuerza la
 * temática naval sin depender de recursos externos ni recargar la pantalla.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class ShipIcon extends Group {

    private static final double MAST_TOP_Y = 0;
    private static final double MAST_BOTTOM_Y = 34;
    private static final double HULL_BOTTOM_Y = 60;
    private static final double CENTER_X = 45;
    private static final double HULL_LEFT_X = 0;
    private static final double HULL_RIGHT_X = 90;
    private static final double HULL_BOTTOM_LEFT_X = 14;
    private static final double HULL_BOTTOM_RIGHT_X = 76;

    /**
     * Construye el ícono y añade sus figuras como hijas de este {@link Group}.
     */
    public ShipIcon() {
        Ellipse waterRipple = new Ellipse(CENTER_X, HULL_BOTTOM_Y + 6, HULL_RIGHT_X / 2 + 14, 8);
        waterRipple.setFill(Color.web("#C9D6DF"));
        waterRipple.setOpacity(0.6);

        Polygon hull = new Polygon(
                HULL_LEFT_X, MAST_BOTTOM_Y,
                HULL_RIGHT_X, MAST_BOTTOM_Y,
                HULL_BOTTOM_RIGHT_X, HULL_BOTTOM_Y,
                HULL_BOTTOM_LEFT_X, HULL_BOTTOM_Y
        );
        hull.setFill(Color.web("#5C6B73"));

        Line mast = new Line(CENTER_X, MAST_TOP_Y, CENTER_X, MAST_BOTTOM_Y);
        mast.setStroke(Color.web("#0B3D62"));
        mast.setStrokeWidth(3);

        Polygon flag = new Polygon(
                CENTER_X, MAST_TOP_Y + 2,
                CENTER_X + 20, MAST_TOP_Y + 8,
                CENTER_X, MAST_TOP_Y + 14
        );
        flag.setFill(Color.web("#D64550"));

        getChildren().addAll(waterRipple, mast, flag, hull);
    }
}
