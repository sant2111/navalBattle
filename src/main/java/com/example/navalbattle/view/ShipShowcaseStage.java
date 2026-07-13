package com.example.navalbattle.view;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.shapes.HitMarkerView;
import com.example.navalbattle.view.shapes.ShipViewFactory;
import com.example.navalbattle.view.shapes.SunkMarkerView;
import com.example.navalbattle.view.shapes.WaterMarkerView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Ventana temporal de previsualización, SOLO para desarrollo: muestra los
 * 4 tipos de barco en ambas orientaciones y los 3 marcadores de estado,
 * para aprobar el diseño visual antes de integrarlo al flujo real del
 * juego.
 * <p>
 * NO forma parte del producto final entregado: se abre con F9 desde
 * {@link WelcomeStage} solo mientras el equipo revisa las figuras, y debe
 * quitarse (junto con ese atajo) antes de la entrega.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class ShipShowcaseStage extends Stage {

    private static final double PREVIEW_CELL_SIZE = BoardCell.CELL_SIZE;
    private static final double WINDOW_WIDTH = 720;
    private static final double WINDOW_HEIGHT = 480;

    /** Construye la ventana de previsualización y su contenido. */
    public ShipShowcaseStage() {
        setTitle("Vista previa de figuras (solo desarrollo)");
        setScene(new Scene(buildRoot(), WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    private VBox buildRoot() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");
        root.getChildren().addAll(buildShipsGrid(), buildMarkersRow());
        return root;
    }

    private GridPane buildShipsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(18);

        grid.add(new Label("Tipo"), 0, 0);
        grid.add(new Label("Horizontal"), 1, 0);
        grid.add(new Label("Vertical"), 2, 0);

        int row = 1;
        for (ShipType type : ShipType.values()) {
            grid.add(new Label(type.getDisplayName()), 0, row);
            grid.add(ShipViewFactory.createShipView(type, PREVIEW_CELL_SIZE, Orientation.HORIZONTAL), 1, row);
            grid.add(ShipViewFactory.createShipView(type, PREVIEW_CELL_SIZE, Orientation.VERTICAL), 2, row);
            row++;
        }
        return grid;
    }

    private HBox buildMarkersRow() {
        HBox row = new HBox(30,
                labeledMarker("Agua", new WaterMarkerView(PREVIEW_CELL_SIZE)),
                labeledMarker("Tocado", new HitMarkerView(PREVIEW_CELL_SIZE)),
                labeledMarker("Hundido", new SunkMarkerView(PREVIEW_CELL_SIZE)));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox labeledMarker(String labelText, Node marker) {
        VBox box = new VBox(6, marker, new Label(labelText));
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
