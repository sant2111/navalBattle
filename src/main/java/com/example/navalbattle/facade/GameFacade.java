package com.example.navalbattle.facade;

import com.example.navalbattle.exceptions.OutOfBoundsShotException;
import com.example.navalbattle.model.*;
import com.example.navalbattle.persistence.GameRepository;
import com.example.navalbattle.persistence.FileGameRepository;


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
    private final GameRepository repository;


    /**
     * Crea una nueva fachada para el motor de juego.
     *
     * @param engine motor principal del juego.
     */
    public GameFacade(GameEngine engine) {
        this.engine = engine;
        this.repository = new FileGameRepository();
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

    public void saveGame() {
        if (engine instanceof DefaultGameEngine defaultGameEngine) {
            repository.save(defaultGameEngine.toSnapshot());
        }
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

    public List<Coordinate> getPlayerShots() {
        return engine.getPlayerShots();
    }

    public List<Coordinate> getAiShots() {
        return engine.getAiShots();
    }

}