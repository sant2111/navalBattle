package com.example.navalbattle.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Primera ventana que ve el jugador: ofrece iniciar una partida nueva,
 * cargar la última guardada o abrir el tablero del oponente en modo
 * verificación, junto con instrucciones de juego y la leyenda de símbolos.
 * <p>
 * Heurísticas de Nielsen aplicadas: 1 (visibilidad del estado — el título
 * y subtítulo dejan claro en qué pantalla está el usuario) y 8 (diseño
 * estético y minimalista — una sola tarjeta centrada, sin ruido visual).
 * El contenido va dentro de un {@code ScrollPane} y la ventana es
 * redimensionable para que ninguna instrucción quede recortada
 * independientemente del tamaño de pantalla del usuario.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class WelcomeStage extends Stage {

    /**
     * Carga {@code welcome-view.fxml}, aplica la hoja de estilos principal
     * y configura esta ventana lista para mostrarse.
     *
     * @throws IOException si el FXML de bienvenida no se puede cargar
     */
    public WelcomeStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UiConstants.WELCOME_VIEW_FXML));
        Parent root = loader.load();

        Scene scene = new Scene(root, UiConstants.WELCOME_WIDTH, UiConstants.WELCOME_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(UiConstants.MAIN_STYLESHEET).toExternalForm());

        setTitle(UiConstants.APP_TITLE);
        setScene(scene);
        setMinWidth(UiConstants.WELCOME_WIDTH);
        setMinHeight(UiConstants.WELCOME_MIN_HEIGHT);
    }
}
