package com.example.navalbattle.facade;

import com.example.navalbattle.exceptions.OutOfBoundsShotException;
import com.example.navalbattle.model.*;

import java.util.List;

/**
 * Fachada del subsistema de juego.
 *
 * <p>Implementa el patrón estructural Facade proporcionando una interfaz
 * simplificada para que los controladores interactúen con el motor del
 * juego sin depender directamente de {@link GameEngine}.</p>
 *
 * <p>Centraliza las operaciones principales de la partida, como realizar
 * disparos, consultar turnos, verificar el estado del juego y obtener
 * información de la flota del jugador.</p>
 *
 * @author Carlos Meneses
 * @version 1.0
 */
public class GameFacade {

    private final GameEngine engine;


    /**
     * Crea una nueva fachada para el motor de juego.
     *
     * @param engine motor principal del juego.
     */
    public GameFacade(GameEngine engine) {
        this.engine = engine;
    }

    public List<ShipPlacement> getPlayerFleet() {
        return engine.getPlayerFleet();
    }

    public ShotOutcome playerShoot(int row, int column)
            throws OutOfBoundsShotException {
        return engine.shootOpponent(row, column);
    }

    public AiShotOutcome playAiTurn() {
        return engine.playAiTurn();
    }

    public boolean isPlayerTurn() {
        return engine.isPlayerTurn();
    }

    public boolean isGameOver() {
        return engine.isGameOver();
    }

    public boolean didPlayerWin() {
        return engine.didPlayerWin();
    }
}