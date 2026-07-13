package com.example.navalbattle;

import com.example.navalbattle.view.WelcomeStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la aplicación JavaFX: al arrancar, muestra la
 * {@link WelcomeStage} como primera pantalla del juego.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class NavalBattleApp extends Application {

    /**
     * Construye y muestra la ventana de bienvenida.
     *
     * @param primaryStage ventana primaria provista por JavaFX (no se usa;
     *                     el juego administra sus propias ventanas {@code Stage})
     * @throws IOException si la vista de bienvenida no se puede cargar
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        WelcomeStage welcomeStage = new WelcomeStage();
        welcomeStage.show();
    }

    /**
     * Lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos, no utilizados
     */
    public static void main(String[] args) {
        launch(args);
    }
}
