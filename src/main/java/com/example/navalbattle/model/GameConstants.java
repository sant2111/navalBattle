package com.example.navalbattle.model;

/**
 * Constantes del dominio del juego, centralizadas para cumplir la regla de
 * "cero variables mágicas": el tamaño del tablero y el rango de índices
 * válidos se definen aquí (y la composición de la flota en {@link ShipType}),
 * nunca hardcodeados dentro de la lógica.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public final class GameConstants {

    /** Cantidad de filas y columnas del tablero (tablero cuadrado 10x10). */
    public static final int BOARD_SIZE = 10;

    /** Índice mínimo válido de fila o columna. */
    public static final int MIN_INDEX = 0;

    /** Índice máximo válido de fila o columna. */
    public static final int MAX_INDEX = BOARD_SIZE - 1;

    /** Constructor privado: es una clase de utilidad, no se instancia. */
    private GameConstants() {
    }
}
