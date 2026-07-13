package com.example.navalbattle.view.shapes;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.GameColors;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

/**
 * Silueta del submarino (3 casillas): casco cilíndrico y bajo, con torre
 * de mando (vela) centrada, periscopio y un par de escotillas.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class SubmarineView extends ShipView {

    /**
     * @param cellSize    tamaño en píxeles de una casilla del tablero
     * @param orientation orientación inicial con la que se dibuja
     */
    public SubmarineView(double cellSize, Orientation orientation) {
        super(ShipType.SUBMARINE, cellSize, orientation);
    }

    @Override
    protected void buildShape() {
        double length = length();
        double thickness = thickness();

        Ellipse hull = new Ellipse(x(length / 2, thickness / 2), y(length / 2, thickness / 2),
                isHorizontal() ? length / 2 : thickness / 2,
                isHorizontal() ? thickness / 2 : length / 2);
        hull.setFill(GameColors.HULL_FILL);
        hull.setStroke(GameColors.HULL_BORDER);
        hull.setStrokeWidth(1);
        getChildren().add(hull);

        double towerAlong = length * 0.42;
        double towerAcross = thickness * 0.1;
        double towerAlongSize = length * 0.16;
        double towerAcrossSize = thickness * 0.55;
        getChildren().add(buildDetailRectangle(towerAlong, towerAcross, towerAlongSize, towerAcrossSize, GameColors.TURRET_DARK));

        double periscopeAlong = towerAlong + towerAlongSize / 2;
        Line periscope = new Line(
                x(periscopeAlong, towerAcross), y(periscopeAlong, towerAcross),
                x(periscopeAlong, towerAcross - thickness * 0.2), y(periscopeAlong, towerAcross - thickness * 0.2)
        );
        periscope.setStroke(GameColors.TURRET_DARK);
        periscope.setStrokeWidth(1.5);
        getChildren().add(periscope);

        Circle periscopeTip = new Circle(
                x(periscopeAlong, towerAcross - thickness * 0.2), y(periscopeAlong, towerAcross - thickness * 0.2), 1.6);
        periscopeTip.setFill(GameColors.TURRET_DARK);
        getChildren().add(periscopeTip);

        Circle hatchOne = new Circle(x(length * 0.22, thickness * 0.5), y(length * 0.22, thickness * 0.5), thickness * 0.08);
        hatchOne.setFill(GameColors.HULL_BORDER);
        Circle hatchTwo = new Circle(x(length * 0.78, thickness * 0.5), y(length * 0.78, thickness * 0.5), thickness * 0.08);
        hatchTwo.setFill(GameColors.HULL_BORDER);
        getChildren().addAll(hatchOne, hatchTwo);
    }
}
