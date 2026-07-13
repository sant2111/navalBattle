package com.example.navalbattle.view.shapes;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.GameColors;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * Silueta de la fragata (1 casilla): el barco más pequeño de la flota,
 * un casco simple con una cabina diminuta y un mástil.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class FrigateView extends ShipView {

    /**
     * @param cellSize    tamaño en píxeles de una casilla del tablero
     * @param orientation orientación inicial con la que se dibuja
     */
    public FrigateView(double cellSize, Orientation orientation) {
        super(ShipType.FRIGATE, cellSize, orientation);
    }

    @Override
    protected void buildShape() {
        double length = length();
        double thickness = thickness();

        Polygon hull = new Polygon(
                x(0, thickness * 0.25), y(0, thickness * 0.25),
                x(0, thickness * 0.75), y(0, thickness * 0.75),
                x(length * 0.7, thickness * 0.85), y(length * 0.7, thickness * 0.85),
                x(length, thickness * 0.5), y(length, thickness * 0.5),
                x(length * 0.7, thickness * 0.15), y(length * 0.7, thickness * 0.15)
        );
        hull.setFill(GameColors.HULL_FILL);
        hull.setStroke(GameColors.HULL_BORDER);
        hull.setStrokeWidth(1);
        getChildren().add(hull);

        getChildren().add(buildDetailRectangle(
                length * 0.35, thickness * 0.3, length * 0.2, thickness * 0.4, GameColors.TURRET_DARK));

        double mastAlong = length * 0.45;
        Line mast = new Line(
                x(mastAlong, thickness * 0.5), y(mastAlong, thickness * 0.5),
                x(mastAlong, thickness * 0.5 - thickness * 0.4), y(mastAlong, thickness * 0.5 - thickness * 0.4)
        );
        mast.setStroke(GameColors.HULL_BORDER);
        mast.setStrokeWidth(1.3);
        getChildren().add(mast);
    }
}
