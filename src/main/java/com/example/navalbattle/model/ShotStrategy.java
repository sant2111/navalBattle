package com.example.navalbattle.model;

/**
 * Estrategia de disparo de la IA (patrón de comportamiento <b>Strategy</b>,
 * según lo pide el enunciado): encapsula el criterio con el que la máquina
 * elige su próxima casilla objetivo, de modo que el motor de juego pueda
 * cambiar de dificultad (aleatoria, "cazar y rematar", etc.) sin modificarse.
 * <p>
 * Contrato: una implementación debe basarse únicamente en el historial de
 * disparos del tablero ({@link Board#hasBeenShot(Coordinate)}) y nunca en las
 * posiciones reales de los barcos —eso sería hacer trampa— y debe devolver una
 * casilla dentro del tablero que aún no haya sido disparada.
 *
 * @author Santiago Barragan
 * @version 1.0
 */
public interface ShotStrategy {

    /**
     * Elige la próxima casilla a la que disparará la IA sobre el tablero rival.
     *
     * @param opponentBoard tablero al que la IA está disparando (para consultar
     *                      qué casillas ya intentó)
     * @return la casilla objetivo elegida, dentro del tablero y no repetida
     */
    Coordinate chooseTarget(Board opponentBoard);
}
