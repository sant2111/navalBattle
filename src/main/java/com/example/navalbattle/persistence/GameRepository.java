package com.example.navalbattle.persistence;

import com.example.navalbattle.exceptions.GameStateCorruptedException;
import com.example.navalbattle.model.GameSnapshot;

/**
 * Contrato mínimo de persistencia que necesita la vista de bienvenida para
 * saber si existe una partida guardada y para cargarla.
 * <p>
 * // TODO: implementa compañero de persistencia — esta interfaz es un STUB
 * creado solo para permitir que {@code controller.WelcomeController}
 * compile e indique el punto de inyección exacto que necesita. El equipo
 * de persistencia debe proveer una implementación real (archivo plano o
 * serialización) y considerar el uso de un patrón Facade sobre ella.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public interface GameRepository {

    /**
     * Indica si existe al menos una partida guardada previamente.
     *
     * @return {@code true} si hay una partida guardada disponible para cargar
     */
    boolean hasSavedGame();

    /**
     * Carga la última partida guardada.
     *
     * @return el estado de la última partida guardada
     * @throws GameStateCorruptedException si el archivo guardado no se puede
     *                                      reconstruir de forma consistente
     */
    GameSnapshot loadLastGame() throws GameStateCorruptedException;
}
