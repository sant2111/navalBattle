package com.example.navalbattle.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Tercera ventana del juego: el layout del tablero de combate, con la
 * flota propia a la vista y las aguas enemigas ocultas.
 * <p>
 * Heurísticas de Nielsen aplicadas: 1 (visibilidad del estado — rótulo
 * de turno siempre visible) y 4 (consistencia — mismas siluetas y
 * marcadores que en la colocación de flota).
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class GameStage extends Stage {

    /**
     * Carga {@code game-view.fxml}, aplica la hoja de estilos principal
     * y configura esta ventana lista para mostrarse.
     *
     * @throws IOException si el FXML de combate no se puede cargar
     */
    public GameStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UiConstants.GAME_VIEW_FXML));
        Parent root = loader.load();

        Scene scene = new Scene(root, UiConstants.GAME_WIDTH, UiConstants.GAME_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(UiConstants.MAIN_STYLESHEET).toExternalForm());

        setTitle(UiConstants.GAME_TITLE);
        setScene(scene);
        setMinWidth(UiConstants.GAME_WIDTH);
        setMinHeight(UiConstants.GAME_MIN_HEIGHT);
    }
}
