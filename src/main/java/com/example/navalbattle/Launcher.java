package com.example.navalbattle;

import javafx.application.Application;

/**
 * Lanzador auxiliar para ejecutar la aplicación desde un JAR modular sin
 * pasar por las restricciones de {@code Application} como clase principal.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class Launcher {

    /**
     * Delega el arranque en {@link NavalBattleApp}.
     *
     * @param args argumentos de línea de comandos, no utilizados
     */
    public static void main(String[] args) {
        Application.launch(NavalBattleApp.class, args);
    }
}
