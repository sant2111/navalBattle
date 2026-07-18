package com.example.navalbattle.view.shapes;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.GameColors;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Silueta del destructor (2 casillas): casco ágil con proa puntiaguda,
 * superestructura central, torreta con cañón y un mástil corto.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class DestroyerView extends ShipView {

    /**
     * @param cellSize    tamaño en píxeles de una casilla del tablero
     * @param orientation orientación inicial con la que se dibuja
     */
    public DestroyerView(double cellSize, Orientation orientation) {
        super(ShipType.DESTROYER, cellSize, orientation);
    }

    @Override
    protected void buildShape() {
        double length = length();
        double thickness = thickness();
        double bowStart = length * 0.75;

        Rectangle sternHull = new Rectangle(
                x(0, 0), y(0, 0),
                isHorizontal() ? bowStart : thickness,
                isHorizontal() ? thickness : bowStart);
        sternHull.setFill(GameColors.HULL_FILL);
        sternHull.setStroke(GameColors.HULL_BORDER);
        sternHull.setStrokeWidth(1);
        getChildren().add(sternHull);

        Polygon bow = new Polygon(
                x(bowStart, 0), y(bowStart, 0),
                x(bowStart, thickness), y(bowStart, thickness),
                x(length, thickness / 2), y(length, thickness / 2)
        );
        bow.setFill(GameColors.HULL_FILL);
        bow.setStroke(GameColors.HULL_BORDER);
        bow.setStrokeWidth(1);
        getChildren().add(bow);

        double structureAlong = length * 0.3;
        double structureAcross = thickness * 0.2;
        double structureAlongSize = length * 0.28;
        double structureAcrossSize = thickness * 0.6;
        getChildren().add(buildDetailRectangle(
                structureAlong, structureAcross, structureAlongSize, structureAcrossSize, GameColors.DECK_DETAIL));

        double turretAlong = structureAlong + structureAlongSize / 2;
        Circle turret = new Circle(x(turretAlong, thickness / 2), y(turretAlong, thickness / 2), thickness * 0.16);
        turret.setFill(GameColors.TURRET_DARK);
        getChildren().add(turret);

        Line cannon = new Line(
                x(turretAlong, thickness / 2), y(turretAlong, thickness / 2),
                x(turretAlong + thickness * 0.5, thickness / 2), y(turretAlong + thickness * 0.5, thickness / 2)
        );
        cannon.setStroke(GameColors.TURRET_DARK);
        cannon.setStrokeWidth(2);
        getChildren().add(cannon);

        double mastAlong = length * 0.15;
        Line mast = new Line(
                x(mastAlong, thickness / 2), y(mastAlong, thickness / 2),
                x(mastAlong, thickness / 2 - thickness * 0.35), y(mastAlong, thickness / 2 - thickness * 0.35)
        );
        mast.setStroke(GameColors.HULL_BORDER);
        mast.setStrokeWidth(1.5);
        getChildren().add(mast);

        Circle mastTip = new Circle(
                x(mastAlong, thickness / 2 - thickness * 0.35), y(mastAlong, thickness / 2 - thickness * 0.35), 1.4);
        mastTip.setFill(GameColors.HULL_BORDER);
        getChildren().add(mastTip);
    }
}
