package com.example.navalbattle.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Un barco ya ubicado en un tablero: conoce su colocación
 * ({@link ShipPlacement}), las casillas que ocupa y cuáles de ellas han sido
 * tocadas. Sabe decir cuándo queda hundido.
 * <p>
 * Se modela como una sola clase (no una jerarquía por tipo) porque el
 * comportamiento —registrar toques y saber si está hundido— es idéntico para
 * todos los barcos; lo único que varía (tamaño y nombre) ya lo aporta
 * {@link ShipType}. Sus instancias las construye {@link ShipFactory}.
 * <p>
 * Guarda su {@link ShipPlacement} original para poder devolverlo cuando el
 * barco se hunde: la vista lo necesita para revelar la silueta completa
 * (heurística 4 de Nielsen: consistencia con la figura mostrada al colocar).
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class Ship {

    private final ShipPlacement placement;
    private final List<Coordinate> occupiedCells;
    private final Set<Coordinate> hitCells;

    /**
     * Crea un barco a partir de su colocación y las casillas ya calculadas.
     * Uso reservado a {@link ShipFactory}, que garantiza que las casillas
     * corresponden a la colocación.
     *
     * @param placement     colocación (tipo, casilla inicial y orientación)
     * @param occupiedCells casillas que ocupa, en orden desde la proa
     */
    Ship(ShipPlacement placement, List<Coordinate> occupiedCells) {
        this.placement = placement;
        this.occupiedCells = List.copyOf(occupiedCells);
        this.hitCells = new HashSet<>();
    }

    /**
     * Registra un impacto en una casilla del barco.
     *
     * @param coordinate casilla impactada
     * @return {@code true} si la casilla pertenece al barco y quedó registrada
     */
    public boolean registerHit(Coordinate coordinate) {
        if (!occupiedCells.contains(coordinate)) {
            return false;
        }
        hitCells.add(coordinate);
        return true;
    }

    /**
     * @param coordinate casilla a consultar
     * @return {@code true} si esa casilla del barco ya fue tocada
     */
    public boolean isHitAt(Coordinate coordinate) {
        return hitCells.contains(coordinate);
    }

    /**
     * @return {@code true} si todas las casillas del barco han sido tocadas
     */
    public boolean isSunk() {
        return hitCells.size() == occupiedCells.size();
    }

    /** @return la colocación (tipo, casilla inicial y orientación) del barco */
    public ShipPlacement getPlacement() {
        return placement;
    }

    /** @return tipo de barco (define su tamaño y nombre) */
    public ShipType getType() {
        return placement.type();
    }

    /** @return casillas que ocupa el barco, en orden desde la proa (lista inmutable) */
    public List<Coordinate> getOccupiedCells() {
        return occupiedCells;
    }
}
