package com.example.navalbattle.model;

/**
 * Orientación de un barco sobre el tablero.
 * <p>
 * // TODO: confirma compañero de modelo si el tablero real necesita algún
 * valor adicional; por ahora solo se admiten las dos orientaciones
 * ortogonales estándar de Batalla Naval.
 *
 * @author Santiago Barragan
 * @version 1.0
 */
public enum Orientation {

    /** El barco ocupa casillas consecutivas en la misma fila. */
    HORIZONTAL,

    /** El barco ocupa casillas consecutivas en la misma columna. */
    VERTICAL
}
