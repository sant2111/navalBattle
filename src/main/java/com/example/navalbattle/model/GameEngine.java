package com.example.navalbattle.model;

import com.example.navalbattle.exceptions.OutOfBoundsShotException;

import java.util.List;

/**
 * Contrato mínimo que necesita la vista de combate para jugar una
 * partida completa: de quién es el turno, cómo se dispara, cómo juega
 * la IA su turno, y cuándo termina la partida.
 * <p>
 * // TODO: implementa compañero de modelo/IA — esta interfaz es un STUB
 * creado solo para permitir que {@code controller.GameController}
 * compile y funcione contra un motor de prueba
 * ({@code controller.MockGameEngine}, temporal) mientras el equipo
 * construye el motor real. El equipo debe implementar esta interfaz (o
 * proponer los ajustes que necesite) con las reglas reales del juego y
 * la IA.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public interface GameEngine {

    /**
     * @return {@code true} si le corresponde disparar al jugador humano
     */
    boolean isPlayerTurn();

    /**
     * Dispara, de parte del jugador, a una celda del tablero del rival.
     *
     * @param row    fila del disparo, de 0 a 9
     * @param column columna del disparo, de 0 a 9
     * @return el resultado del disparo
     * @throws OutOfBoundsShotException si la coordenada está fuera del tablero
     */
    ShotOutcome shootOpponent(int row, int column) throws OutOfBoundsShotException;

    /**
     * Ejecuta el turno completo de la IA: decide dónde dispara sobre el
     * tablero del jugador y devuelve el resultado, para que la vista lo
     * pinte en el tablero propio.
     *
     * @return la celda y el resultado del disparo de la IA
     */
    AiShotOutcome playAiTurn();

    /** @return {@code true} si la partida ya terminó (un bando sin flota) */
    boolean isGameOver();

    /** @return {@code true} si quien ganó la partida fue el jugador humano */
    boolean didPlayerWin();

    /** @return la flota del jugador, para que la vista la dibuje en su propio tablero */
    List<ShipPlacement> getPlayerFleet();
}
