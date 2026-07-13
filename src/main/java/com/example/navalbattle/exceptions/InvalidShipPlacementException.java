package com.example.navalbattle.exceptions;

/**
 * Excepción marcada (checked) que señala una colocación de barco inválida
 * sobre el tablero: solapamiento con otro barco, salida de los límites de
 * la grilla o violación de la separación mínima entre barcos exigida por
 * las reglas del juego.
 * <p>
 * Se lanza durante el arrastre (drag-and-drop) de barcos en la vista de
 * colocación, y debe capturarse para mostrar al usuario un mensaje de
 * recuperación de error claro (heurística 9 de Nielsen), en lugar de
 * dejar que la aplicación falle.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class InvalidShipPlacementException extends Exception {

    /**
     * Crea la excepción con un mensaje descriptivo del motivo de la
     * colocación inválida.
     *
     * @param message descripción legible del error, apta para mostrar al usuario
     */
    public InvalidShipPlacementException(String message) {
        super(message);
    }

    /**
     * Crea la excepción encadenando la causa original.
     *
     * @param message descripción legible del error, apta para mostrar al usuario
     * @param cause   excepción original que originó este error
     */
    public InvalidShipPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}
