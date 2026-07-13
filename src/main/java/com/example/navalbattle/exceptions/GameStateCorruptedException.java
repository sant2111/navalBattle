package com.example.navalbattle.exceptions;

/**
 * Excepción no marcada (unchecked) que señala que el estado de una partida
 * guardada no se pudo reconstruir de forma consistente (archivo plano o
 * serialización corrupta, incompleta o con formato inesperado).
 * <p>
 * Es no marcada porque representa una falla de datos externos al flujo
 * normal del programa: la capa de persistencia la lanza al leer, y la capa
 * visual la captura puntualmente donde tenga sentido mostrar un mensaje de
 * recuperación de error (heurística 9), sin obligar a declararla en cada
 * método intermedio.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class GameStateCorruptedException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo del motivo del error.
     *
     * @param message descripción legible del error, apta para mostrar al usuario
     */
    public GameStateCorruptedException(String message) {
        super(message);
    }

    /**
     * Crea la excepción encadenando la causa original.
     *
     * @param message descripción legible del error, apta para mostrar al usuario
     * @param cause   excepción original que originó este error
     */
    public GameStateCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
