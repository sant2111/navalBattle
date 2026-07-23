package com.example.navalbattle.model;

/**
 * Resultado completo del turno de la IA: en qué celda del tablero propio
 * disparó y con qué resultado.
 * <p>
 * // TODO: confirma compañero de modelo/IA.
 *
 * @param row     fila donde disparó la IA, de 0 a 9
 * @param column  columna donde disparó la IA, de 0 a 9
 * @param outcome resultado de ese disparo
 * @author Santiago Barragan
 * @version 1.0
 */
public record AiShotOutcome(int row, int column, ShotOutcome outcome) {
}
