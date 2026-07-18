package com.example.navalbattle.view;

/**
 * Constantes de la capa visual: ninguna vista o controlador debe usar
 * literales mágicos para textos, rutas de recursos o dimensiones.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public final class UiConstants {

    /** Mensaje mostrado cuando la partida guardada no se puede leer. */
    public static final String CORRUPTED_SAVE_MESSAGE =
            "La partida guardada no se pudo leer. Puedes iniciar un nuevo juego.";

    /** Ruta del FXML de la vista de bienvenida. */
    public static final String WELCOME_VIEW_FXML = "/com/example/navalbattle/view/welcome-view.fxml";

    /** Ruta de la hoja de estilos principal. */
    public static final String MAIN_STYLESHEET = "/com/example/navalbattle/styles/main.css";

    /** Título de la aplicación. */
    public static final String APP_TITLE = "Batalla Naval";

    /** Ancho por defecto de la ventana de bienvenida. */
    public static final double WELCOME_WIDTH = 520;

    /** Alto por defecto de la ventana de bienvenida. */
    public static final double WELCOME_HEIGHT = 760;

    /** Alto mínimo permitido de la ventana de bienvenida. */
    public static final double WELCOME_MIN_HEIGHT = 480;

    /** Ruta del FXML de la vista de colocación de flota. */
    public static final String BOARD_SETUP_VIEW_FXML = "/com/example/navalbattle/view/board-setup-view.fxml";

    /** Título de la ventana de colocación de flota. */
    public static final String BOARD_SETUP_TITLE = APP_TITLE + " — Colocación de flota";

    /** Ancho por defecto de la ventana de colocación de flota. */
    public static final double BOARD_SETUP_WIDTH = 760;

    /** Alto por defecto de la ventana de colocación de flota. */
    public static final double BOARD_SETUP_HEIGHT = 680;

    /** Alto mínimo permitido de la ventana de colocación de flota. */
    public static final double BOARD_SETUP_MIN_HEIGHT = 560;

    /** Ruta del FXML de la vista de combate. */
    public static final String GAME_VIEW_FXML = "/com/example/navalbattle/view/game-view.fxml";

    /** Título de la ventana de combate. */
    public static final String GAME_TITLE = APP_TITLE + " — Combate";

    /** Ancho por defecto de la ventana de combate. */
    public static final double GAME_WIDTH = 820;

    /** Alto por defecto de la ventana de combate. */
    public static final double GAME_HEIGHT = 620;

    /** Alto mínimo permitido de la ventana de combate. */
    public static final double GAME_MIN_HEIGHT = 500;

    private UiConstants() {
    }
}
