package com.example.navalbattle.model;

/**
 * Coordenada de fila/columna dentro del tablero, en la capa {@code model}.
 * <p>
 * Es el tipo de coordenada propio del modelo: la vista usa su propio
 * {@code view.GridCoordinate} y el controlador convierte entre ambos en la
 * frontera. Así el modelo nunca depende de la vista y se respeta MVC.
 * <p>
 * Por ser {@code record}, dos coordenadas con la misma fila y columna son
 * iguales ({@code equals}/{@code hashCode} automáticos), lo que permite usarla
 * como clave en conjuntos y comparar casillas de barcos y disparos.
 *
 * @param row    fila, válida entre {@link GameConstants#MIN_INDEX} y {@link GameConstants#MAX_INDEX}
 * @param column columna, válida entre {@link GameConstants#MIN_INDEX} y {@link GameConstants#MAX_INDEX}
 * @author Jorge Navia
 * @version 1.0
 */
public record Coordinate(int row, int column) {

    /**
     * Indica si la coordenada cae dentro de los límites del tablero.
     * <p>
     * No se valida en el constructor a propósito: el tablero necesita poder
     * recibir una coordenada fuera de rango para lanzar
     * {@code OutOfBoundsShotException}, de modo que la validación vive en quien
     * consume la coordenada, no en su creación.
     *
     * @return {@code true} si fila y columna están en el rango válido 0..9
     */
    public boolean isWithinBoard() {
        return row >= GameConstants.MIN_INDEX && row <= GameConstants.MAX_INDEX
                && column >= GameConstants.MIN_INDEX && column <= GameConstants.MAX_INDEX;
    }
}
