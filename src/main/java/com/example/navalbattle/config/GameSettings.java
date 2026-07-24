package com.example.navalbattle.config;

/**
 * Centraliza la configuración global de la aplicación utilizada durante la
 * ejecución de una partida.
 * <p>
 * Actualmente permite controlar si debe mostrarse la ventana de verificación
 * del tablero del oponente antes de iniciar el juego.
 *
 * @author Carlos Meneses
 * @version 1.0
 */
public final class GameSettings {

    /**
     * Evita la instanciación de esta clase utilitaria.
     */
    private GameSettings() {
    }

    /**
     * Indica si debe mostrarse la ventana de verificación del tablero del
     * oponente antes de comenzar la partida.
     */
    private static boolean verifyOpponentBoard = false;

    /**
     * Indica si la verificación del tablero del oponente está habilitada.
     *
     * @return {@code true} si debe mostrarse la ventana de verificación;
     *         {@code false} en caso contrario
     */
    public static boolean isVerifyOpponentBoard() {
        return verifyOpponentBoard;
    }

    /**
     * Activa o desactiva la verificación del tablero del oponente.
     *
     * @param value {@code true} para habilitar la verificación;
     *              {@code false} para deshabilitarla
     */
    public static void setVerifyOpponentBoard(boolean value) {
        verifyOpponentBoard = value;
    }
}