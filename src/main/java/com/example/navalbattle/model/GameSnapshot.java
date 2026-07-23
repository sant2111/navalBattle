package com.example.navalbattle.model;

import java.io.Serializable;
import java.util.List;

/**
 * Estado serializable de una partida guardada: todo lo necesario para
 * reconstruir el motor de juego al cargar. Es el contrato entre el modelo y la
 * capa de persistencia (lo devuelve {@code persistence.GameRepository#loadLastGame()}
 * y lo produce {@link DefaultGameEngine#toSnapshot()}).
 * <p>
 * Cumple el requisito del enunciado "serializables (tableros)": este DTO es la
 * representación serializable del estado de ambos tableros. Se serializa un
 * objeto de datos plano —no los objetos de dominio vivos ({@link Board} /
 * {@link Ship}), que conservan su lógica—: el motor los reconstruye a partir de
 * aquí volviendo a colocar las flotas y a aplicar los disparos.
 * <p>
 * Al ser un {@code record} que implementa {@link Serializable} y contener solo
 * tipos serializables (records de la frontera y enums), no expone campos
 * mutables ni requiere lógica de serialización manual.
 * <p>
 * ⚠️ IMPACTO AL EQUIPO (persistencia): esta es la forma acordada del estado a
 * guardar. Falta además que {@code GameRepository} incluya un método
 * {@code save(GameSnapshot)} (hoy solo declara la carga).
 *
 * @param playerFleet colocaciones de la flota del jugador
 * @param aiFleet     colocaciones de la flota de la máquina
 * @param playerShots casillas que el jugador disparó sobre el tablero de la IA
 * @param aiShots     casillas que la IA disparó sobre el tablero del jugador
 * @param playerTurn  {@code true} si al guardar era el turno del jugador
 * @author Santiago Barragan
 * @version 1.0
 */
public record GameSnapshot(
        List<ShipPlacement> playerFleet,
        List<ShipPlacement> aiFleet,
        List<Coordinate> playerShots,
        List<Coordinate> aiShots,
        boolean playerTurn) implements Serializable {

    /** Identificador de versión para la serialización de la partida guardada. */
    private static final long serialVersionUID = 1L;
}
