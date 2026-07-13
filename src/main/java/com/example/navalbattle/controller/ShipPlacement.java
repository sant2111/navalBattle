package com.example.navalbattle.controller;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import com.example.navalbattle.view.GridCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro interno de un barco ya colocado en la grilla de configuración:
 * qué tipo es, en qué celda inicia y con qué orientación. Se usa solo
 * dentro de {@link BoardSetupController} para poder deshacer colocaciones
 * (pila) y para calcular qué celdas ocupa un barco, tanto ya colocado
 * como durante la previsualización del arrastre.
 *
 * @author Jorge Navia
 * @version 1.0
 */
final class ShipPlacement {

    private final ShipType type;
    private final int startRow;
    private final int startColumn;
    private final Orientation orientation;

    ShipPlacement(ShipType type, int startRow, int startColumn, Orientation orientation) {
        this.type = type;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.orientation = orientation;
    }

    ShipType getType() {
        return type;
    }

    GridCoordinate getStart() {
        return new GridCoordinate(startRow, startColumn);
    }

    Orientation getOrientation() {
        return orientation;
    }

    /**
     * Calcula las celdas que ocuparía un barco del tipo, posición y
     * orientación dados, sin necesidad de haberlo colocado todavía.
     *
     * @param type        tipo de barco (define cuántas celdas ocupa)
     * @param startRow    fila donde inicia el barco
     * @param startColumn columna donde inicia el barco
     * @param orientation orientación del barco
     * @return lista de coordenadas ocupadas, en orden desde el inicio
     */
    static List<GridCoordinate> computeCells(ShipType type, int startRow, int startColumn, Orientation orientation) {
        List<GridCoordinate> cells = new ArrayList<>();
        boolean isHorizontal = orientation == Orientation.HORIZONTAL;
        for (int offset = 0; offset < type.getSizeInCells(); offset++) {
            int row = isHorizontal ? startRow : startRow + offset;
            int column = isHorizontal ? startColumn + offset : startColumn;
            cells.add(new GridCoordinate(row, column));
        }
        return cells;
    }

    List<GridCoordinate> occupiedCells() {
        return computeCells(type, startRow, startColumn, orientation);
    }
}
