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

    /**
     * Obtiene la flota del jugador con sus posiciones actuales.
     *
     * @return lista de barcos colocados por el jugador.
     */
    public List<ShipPlacement> getPlayerFleet() {
        return engine.getPlayerFleet();
    }

    /**
     * Realiza un disparo sobre el tablero del oponente.
     *
     * @param row fila objetivo.
     * @param column columna objetivo.
     * @return resultado del disparo realizado.
     * @throws OutOfBoundsShotException si la posición indicada está fuera
     *                                  de los límites del tablero.
     */
    public ShotOutcome playerShoot(int row, int column)
            throws OutOfBoundsShotException {
        return engine.shootOpponent(row, column);
    }

    /**
     * Ejecuta el turno de la inteligencia artificial.
     *
     * @return información del disparo realizado por la IA.
     */
    public AiShotOutcome playAiTurn() {
        return engine.playAiTurn();
    }

    /**
     * Guarda el estado actual de la partida utilizando el repositorio de
     * persistencia.
     */
    public void saveGame() {
        if (engine instanceof DefaultGameEngine defaultGameEngine) {
            repository.save(defaultGameEngine.toSnapshot());
        }
    }

    /**
     * Indica si actualmente corresponde el turno del jugador.
     *
     * @return {@code true} si es el turno del jugador;
     *         {@code false} en caso contrario.
     */
    public boolean isPlayerTurn() {
        return engine.isPlayerTurn();
    }

    /**
     * Determina si la partida ha finalizado.
     *
     * @return {@code true} si el juego terminó;
     *         {@code false} en caso contrario.
     */
    public boolean isGameOver() {
        return engine.isGameOver();
    }

    /**
     * Indica si el jugador ganó la partida.
     *
     * @return {@code true} si el jugador es el ganador;
     *         {@code false} en caso contrario.
     */
    public boolean didPlayerWin() {
        return engine.didPlayerWin();
    }

    /**
     * Obtiene el historial de disparos realizados por el jugador.
     *
     * @return lista de coordenadas disparadas por el jugador.
     */
    public List<Coordinate> getPlayerShots() {
        return engine.getPlayerShots();
    }

    /**
     * Obtiene el historial de disparos realizados por la inteligencia
     * artificial.
     *
     * @return lista de coordenadas disparadas por la IA.
     */
    public List<Coordinate> getAiShots() {
        return engine.getAiShots();
    }

    /**
     * Obtiene el tablero correspondiente al jugador.
     *
     * @return tablero del jugador.
     */
    public Board getPlayerBoard() {
        return engine.getPlayerBoard();
    }

    /**
     * Obtiene el tablero correspondiente al oponente.
     *
     * @return tablero del oponente.
     */
    public Board getOpponentBoard() {
        return engine.getOpponentBoard();
    }

}