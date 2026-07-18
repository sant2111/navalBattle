package com.example.navalbattle.exceptions;

/**
 * Excepción marcada (checked) que señala que un disparo fue dirigido a una
 * coordenada fuera de los límites del tablero (fuera del rango A-J / 1-10).
 * <p>
 * En condiciones normales la vista impide llegar a este caso mediante
 * snapping a la grilla (heurística 5 de Nielsen: prevención de errores),
 * pero la excepción existe como resguardo defensivo para cualquier entrada
 * de coordenadas que no pase por la interacción gráfica estándar.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class OutOfBoundsShotException extends Exception {

    /**
     * Crea la excepción con un mensaje descriptivo del motivo del error.
     *
     * @param message descripción legible del error, apta para mostrar al usuario
     */
    public OutOfBoundsShotException(String message) {
        super(message);
    }

    /**
     * Crea la excepción encadenando la causa original.
     *
     * @param message descripción legible del error, apta para mostrar al usuario
     * @param cause   excepción original que originó este error
     */
    public OutOfBoundsShotException(String message, Throwable cause) {
        super(message, cause);
    }
}
