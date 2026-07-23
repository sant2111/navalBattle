package com.example.navalbattle.controller;

import com.example.navalbattle.exceptions.GameStateCorruptedException;
import com.example.navalbattle.model.GameSnapshot;
import com.example.navalbattle.persistence.GameRepository;
import com.example.navalbattle.view.BoardSetupStage;
import com.example.navalbattle.view.UiConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.example.navalbattle.model.DefaultGameEngine;
import com.example.navalbattle.view.GameStage;
import java.io.IOException;

/**
 * Controlador de {@code welcome-view.fxml}: despacha las tres acciones
 * iniciales del juego (nueva partida, cargar última partida, ver tablero
 * del oponente en modo verificación) y muestra la leyenda de colores e
 * instrucciones de juego.
 * <p>
 * Heurísticas de Nielsen aplicadas:
 * <ul>
 *   <li>1 — Visibilidad del estado: "Cargar último juego" refleja si hay
 *       una partida guardada disponible.</li>
 *   <li>4 — Consistencia y estándares: la leyenda de colores (agua,
 *       tocado, hundido) usa la misma paleta que se reutilizará en el
 *       tablero de juego.</li>
 *   <li>6 — Reconocimiento en lugar de recuerdo: la leyenda de colores y
 *       los pasos numerados evitan que el jugador deba recordar las
 *       reglas de memoria.</li>
 *   <li>9 — Recuperación de errores: mensaje claro y no técnico si la
 *       partida guardada está corrupta.</li>
 *   <li>10 — Ayuda y documentación: sección de instrucciones siempre
 *       visible en la propia pantalla de bienvenida.</li>
 * </ul>
 * <p>
 * Eventos escuchados: clic en los tres botones de acción.
 * <p>
 * Métodos de otras capas invocados (vía interfaz, pendientes de
 * implementación real del equipo): {@link GameRepository#hasSavedGame()} y
 * {@link GameRepository#loadLastGame()}.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class WelcomeController {

    @FXML
    private Button newGameButton;

    @FXML
    private Button loadGameButton;

    @FXML
    private Button viewOpponentBoardButton;

    private GameRepository gameRepository;

    /**
     * Punto de inyección del repositorio de partidas guardadas: quien
     * ensamble la aplicación (Launcher/App) debe llamar a este método con
     * la implementación real antes de mostrar la ventana.
     * <p>
     * ⚠️ IMPACTO AL EQUIPO: contrato pendiente de acordar con el
     * compañero de persistencia — de aquí depende si "Cargar último
     * juego" queda habilitado.
     *
     * @param gameRepository implementación real de {@link GameRepository}
     */
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        loadGameButton.setDisable(gameRepository == null || !gameRepository.hasSavedGame());
    }

    /**
     * Inicializa el estado visual del formulario: "Cargar último juego"
     * arranca deshabilitado hasta que se inyecte un repositorio con datos.
     */
    @FXML
    private void initialize() {
        loadGameButton.setDisable(true);
    }

    /**
     * Maneja el clic en "Nuevo juego": abre {@link BoardSetupStage} y
     * oculta esta ventana (no la cierra) para poder volver a mostrarla
     * si el jugador presiona "Volver" en la siguiente pantalla.
     */
    @FXML
    private void handleNewGameAction() {
        try {
            BoardSetupStage boardSetupStage = new BoardSetupStage();
            boardSetupStage.getController().setPreviousStage(currentStage());
            boardSetupStage.show();
            currentStage().hide();
        } catch (IOException exception) {
            showErrorAlert("No se pudo abrir la pantalla de colocación de barcos.");
        }
    }

    private Stage currentStage() {
        return (Stage) newGameButton.getScene().getWindow();
    }

    /**
     * Maneja el clic en "Cargar último juego": delega en
     * {@link GameRepository#loadLastGame()} y captura la corrupción de
     * datos con un mensaje no técnico (heurística 9).
     */
    @FXML
    private void handleLoadGameAction() {
        if (gameRepository == null) {
            return;
        }
        try {

            GameSnapshot snapshot = gameRepository.loadLastGame();

            GameStage gameStage = new GameStage();
            gameStage.getController().setGameEngine(new DefaultGameEngine(snapshot));

            gameStage.show();
            currentStage().close();

        } catch (GameStateCorruptedException exception) {
            showErrorAlert(UiConstants.CORRUPTED_SAVE_MESSAGE);
        } catch (IOException exception) {
            showErrorAlert("No se pudo abrir la partida guardada.");
        }
    }

    /**
     * Maneja el clic en "Ver tablero del oponente (verificación)" (HU-3):
     * abre una vista de solo lectura con el tablero de la IA antes de
     * iniciar la partida.
     */
    @FXML
    private void handleViewOpponentBoardAction() {
        // TODO: HU-3 — mostrar el tablero del oponente en modo verificación
        // cuando el modelo exponga el tablero/IA generados.
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
