package com.example.navalbattle.view;

import com.example.navalbattle.model.DefaultGameEngine;
import com.example.navalbattle.model.ShipPlacement;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OpponentBoardVerificationStage extends Stage {

    public OpponentBoardVerificationStage(DefaultGameEngine engine) {

        setTitle("Verificación del tablero del oponente");

        GridBoardPane board = new GridBoardPane();

        for (ShipPlacement ship : engine.getOpponentFleet()) {

            board.placeShipFigure(
                    new GridCoordinate(
                            ship.startRow(),
                            ship.startColumn()
                    ),
                    ship.type(),
                    ship.orientation()
            );
        }

        Button closeButton = new Button("Cerrar tablero");
        closeButton.setOnAction(event -> close());

        HBox buttonBox = new HBox(closeButton);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-alignment: center;");

        BorderPane root = new BorderPane();
        root.setCenter(board);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root);

        setScene(scene);

        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        setAlwaysOnTop(true);
    }
}