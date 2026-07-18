package com.example.navalbattle.view;

import com.example.navalbattle.controller.BoardSetupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Segunda ventana del juego: el jugador coloca su flota fija (1
 * portaaviones, 2 submarinos, 3 destructores, 4 fragatas) arrastrándola
 * sobre una grilla de 10x10.
 * <p>
 * Heurísticas de Nielsen aplicadas: 1 (visibilidad — contador de barcos
 * pendientes y botón "Comenzar partida" solo habilitado con la flota
 * completa) y 5 (prevención de errores — resaltado válido/inválido
 * durante el arrastre).
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class BoardSetupStage extends Stage {

    private final BoardSetupController controller;

    /**
     * Carga {@code board-setup-view.fxml}, aplica la hoja de estilos
     * principal y configura esta ventana lista para mostrarse.
     *
     * @throws IOException si el FXML de colocación no se puede cargar
     */
    public BoardSetupStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UiConstants.BOARD_SETUP_VIEW_FXML));
        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root, UiConstants.BOARD_SETUP_WIDTH, UiConstants.BOARD_SETUP_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(UiConstants.MAIN_STYLESHEET).toExternalForm());

        setTitle(UiConstants.BOARD_SETUP_TITLE);
        setScene(scene);
        setMinWidth(UiConstants.BOARD_SETUP_WIDTH);
        setMinHeight(UiConstants.BOARD_SETUP_MIN_HEIGHT);
    }

    /**
     * @return el controlador cargado desde el FXML, para que quien abra
     *         esta ventana pueda inyectarle dependencias (por ejemplo, la
     *         ventana anterior a la que volver)
     */
    public BoardSetupController getController() {
        return controller;
    }
}
