package com.example.navalbattle.view.shapes;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.GameColors;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Silueta del portaaviones (4 casillas): el barco más grande y plano de
 * la flota. Se distingue por su cubierta de vuelo clara, su torre de
 * mando lateral y las marcas punteadas de la pista.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class CarrierView extends ShipView {

    private static final int RUNWAY_STRIPE_COUNT = 3;
    private static final double STRIPE_INSET = 4;

    /**
     * @param cellSize    tamaño en píxeles de una casilla del tablero
     * @param orientation orientación inicial con la que se dibuja
     */
    public CarrierView(double cellSize, Orientation orientation) {
        super(ShipType.CARRIER, cellSize, orientation);
    }

    @Override
    protected void buildShape() {
        double length = length();
        double thickness = thickness();

        Rectangle hull = new Rectangle(hullWidth(), hullHeight());
        hull.setArcWidth(8);
        hull.setArcHeight(8);
        hull.setFill(GameColors.HULL_FILL);
        hull.setStroke(GameColors.HULL_BORDER);
        hull.setStrokeWidth(1);
        getChildren().add(hull);

        Polygon bow = new Polygon(
                x(length, 0), y(length, 0),
                x(length, thickness), y(length, thickness),
                x(length + thickness * 0.4, thickness / 2), y(length + thickness * 0.4, thickness / 2)
        );
        bow.setFill(GameColors.HULL_FILL);
        bow.setStroke(GameColors.HULL_BORDER);
        bow.setStrokeWidth(1);
        getChildren().add(bow);

        double deckAlong = length * 0.05;
        double deckAcross = thickness * 0.15;
        double deckAlongSize = length * 0.85;
        double deckAcrossSize = thickness * 0.7;
        getChildren().add(buildDetailRectangle(deckAlong, deckAcross, deckAlongSize, deckAcrossSize, GameColors.DECK_DETAIL));

        getChildren().add(buildDetailRectangle(
                length * 0.65, thickness * 0.05, length * 0.15, thickness * 0.3, GameColors.TURRET_DARK));

        for (int stripe = 0; stripe < RUNWAY_STRIPE_COUNT; stripe++) {
            double across = thickness * (0.3 + stripe * 0.2);
            Line runwayStripe = new Line(
                    x(deckAlong + STRIPE_INSET, across), y(deckAlong + STRIPE_INSET, across),
                    x(deckAlong + deckAlongSize - STRIPE_INSET, across), y(deckAlong + deckAlongSize - STRIPE_INSET, across)
            );
            runwayStripe.setStroke(GameColors.RUNWAY_STRIPE);
            runwayStripe.setStrokeWidth(1.2);
            runwayStripe.getStrokeDashArray().addAll(4.0, 3.0);
            getChildren().add(runwayStripe);
        }
    }
}
