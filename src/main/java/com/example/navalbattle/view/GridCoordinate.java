package com.example.navalbattle.view;

/**
 * Coordenada de fila/columna (ambas de 0 a 9) dentro de una
 * {@link GridBoardPane}.
 *
 * @param row    fila, de 0 a 9
 * @param column columna, de 0 a 9
 * @author Jorge Navia
 * @version 1.0
 */
public record GridCoordinate(int row, int column) {
}
