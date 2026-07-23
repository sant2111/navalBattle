package com.example.navalbattle.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica de barcos (patrón creacional <b>Factory Method</b>, según lo pide el
 * enunciado): centraliza la construcción de {@link Ship}. El resto del modelo
 * pide "créame el barco de esta colocación" sin conocer cómo se calculan las
 * casillas que ocupa ni cómo se ensambla el objeto.
 * <p>
 * Es el equivalente en el modelo de {@code view.shapes.ShipViewFactory} (que
 * fabrica la figura 2D): uno crea el barco lógico, el otro su dibujo, a partir
 * del mismo {@link ShipType}.
 * <p>
 * // TODO(equipo): si el profesor exige Factory Method GoF estricto (jerarquía
 * de creadores con una subclase por tipo de barco), esta clase se puede
 * escalar. Se mantiene simple y explicable como pide el CLAUDE.md, ya que el
 * comportamiento no varía por tipo (solo el tamaño, que aporta {@link ShipType}).
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class ShipFactory {

    /**
     * Método fábrica: crea el barco de una colocación, calculando internamente
     * todas las casillas que ocupará.
     *
     * @param placement colocación deseada (tipo, casilla inicial y orientación)
     * @return el barco construido, listo para colocarse en un {@link Board}
     */
    public Ship create(ShipPlacement placement) {
        List<Coordinate> cells = computeCells(placement);
        return new Ship(placement, cells);
    }

    /**
     * Calcula, sin construir el barco, las casillas que ocuparía una
     * colocación dada. Se expone como utilidad porque tanto la creación del
     * barco como una validación previa de "¿cabe aquí?" necesitan el mismo
     * cálculo.
     *
     * @param placement colocación (tipo, casilla inicial y orientación)
     * @return lista de casillas ocupadas, en orden desde la proa
     */
    public static List<Coordinate> computeCells(ShipPlacement placement) {
        List<Coordinate> cells = new ArrayList<>();
        boolean horizontal = placement.orientation() == Orientation.HORIZONTAL;
        int size = placement.type().getSizeInCells();
        for (int offset = 0; offset < size; offset++) {
            int row = horizontal ? placement.startRow() : placement.startRow() + offset;
            int column = horizontal ? placement.startColumn() + offset : placement.startColumn();
            cells.add(new Coordinate(row, column));
        }
        return cells;
    }
}
