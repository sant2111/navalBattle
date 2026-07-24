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
 * @author Santiago Barragan
 * @version 1.0
 */
public interface GameEngine {

    /**
     * Indica si actualmente corresponde el turno del jugador humano.
     *
     * @return {@code true} si le corresponde disparar al jugador humano
     */
    boolean isPlayerTurn();

    /**
     * Dispara, de parte del jugador, a una celda del tablero del rival.
     *
     * @param row fila del disparo, de 0 a 9
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

    /**
     * Determina si la partida ha finalizado.
     *
     * @return {@code true} si la partida ya terminó (un bando sin flota)
     */
    boolean isGameOver();

    /**
     * Indica si el ganador de la partida fue el jugador humano.
     *
     * @return {@code true} si quien ganó la partida fue el jugador humano
     */
    boolean didPlayerWin();

    /**
     * Obtiene la flota del jugador para ser representada en la vista.
     *
     * @return flota del jugador
     */
    List<ShipPlacement> getPlayerFleet();

    /**
     * Devuelve la flota del oponente (la máquina), para la opción de
     * verificación que exige el enunciado: "visualizar el tablero de posición
     * del oponente".
     * <p>
     * Es un método por defecto para no romper la compatibilidad: un motor que
     * no soporte la verificación (por ejemplo, el motor de prueba temporal)
     * hereda esta versión, que devuelve una lista vacía, sin necesidad de
     * modificarse. El motor real lo sobrescribe.
     *
     * @return las colocaciones de la flota de la máquina, o lista vacía si el
     *         motor no expone esta información
     */
    default List<ShipPlacement> getOpponentFleet() {
        return List.of();
    }

    /**
     * Obtiene el historial de disparos realizados por el jugador durante la
     * partida.
     *
     * @return lista de coordenadas disparadas por el jugador
     */
    List<Coordinate> getPlayerShots();

    /**
     * Obtiene el historial de disparos realizados por la inteligencia
     * artificial.
     *
     * @return lista de coordenadas disparadas por la IA
     */
    List<Coordinate> getAiShots();

    /**
     * Obtiene el tablero correspondiente al jugador.
     *
     * @return tablero del jugador
     */
    Board getPlayerBoard();

    /**
     * Obtiene el tablero correspondiente al oponente.
     *
     * @return tablero del oponente
     */
    Board getOpponentBoard();
}