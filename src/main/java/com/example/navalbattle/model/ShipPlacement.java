package com.example.navalbattle.model;

import java.io.Serializable;

/**
 * Contrato público (a través de la frontera modelo/vista) de dónde está
 * colocado un barco: tipo, celda inicial y orientación.
 * <p>
 * // TODO: confirma compañero de modelo — esta es la forma mínima que la
 * vista necesita para dibujar un barco ({@code ShipViewFactory}) y para
 * guardar/restaurar partidas. Es intencionalmente distinto de
 * {@code controller.ShipPlacement} (que es de uso interno y exclusivo de
 * la grilla de colocación): este es el tipo que debe cruzar hacia/desde
 * el modelo.
 *
 * @param type        tipo de barco
 * @param startRow    fila donde inicia el barco, de 0 a 9
 * @param startColumn columna donde inicia el barco, de 0 a 9
 * @param orientation orientación del barco
 * @author Santiago Barragan
 * @version 1.0
 */
public record ShipPlacement(ShipType type, int startRow, int startColumn, Orientation orientation)
        implements Serializable {

    /** Identificador de versión para la serialización de la partida guardada. */
    private static final long serialVersionUID = 1L;
}
