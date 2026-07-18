package com.example.navalbattle.controller;

import com.example.navalbattle.view.GridBoardPane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador de {@code game-view.fxml}: por ahora arma únicamente el
 * layout del tablero de combate (flota propia a la vista + aguas
 * enemigas), con el rótulo de turno inicializado en un texto de
 * ejemplo.
 * <p>
 * La lógica de turnos, disparos, resultados e IA la conecta el equipo
 * de modelo/IA sobre esta misma vista.
 * <p>
 * Heurística 1 de Nielsen (visibilidad del estado): el rótulo de turno
 * queda siempre visible en la parte superior.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class GameController {

    @FXML
    private GridBoardPane ownBoard;

    @FXML
    private GridBoardPane enemyBoard;

    @FXML
    private Label turnLabel;

    /** Inicializa el rótulo de turno con un texto de ejemplo. */
    @FXML
    private void initialize() {
        turnLabel.setText("Tu turno");
    }
}
