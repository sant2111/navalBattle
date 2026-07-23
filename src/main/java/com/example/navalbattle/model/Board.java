package com.example.navalbattle.model;

import com.example.navalbattle.exceptions.InvalidShipPlacementException;
import com.example.navalbattle.exceptions.OutOfBoundsShotException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tablero 10x10 de un jugador. Es la estructura de datos tipo <b>Tabla</b> del
 * modelo: una matriz {@code Ship[fila][columna]} donde {@code null} representa
 * agua. Mantiene además la <b>Lista</b> de barcos (flota) y el conjunto de
 * casillas ya disparadas.
 * <p>
 * Responsabilidades: colocar barcos validando que quepan y no se solapen
 * ({@link InvalidShipPlacementException}), resolver un disparo clasificándolo
 * en agua/tocado/hundido ({@link ShotResult}) y saber cuándo toda la flota fue
 * hundida (fin de partida). Las <b>reglas de turno</b> (quién vuelve a
 * disparar) no viven aquí sino en el motor: el tablero solo resuelve el
 * disparo individual.
 * <p>
 * No valida separación mínima entre barcos porque el enunciado no la exige
 * (los barcos pueden quedar adyacentes).
 *
 * @author Santiago Barragan
 * @version 1.0
 */
public class Board {

    private final Ship[][] grid;
    private final List<Ship> fleet;
    private final Set<Coordinate> firedShots;

    /** Crea un tablero vacío de {@link GameConstants#BOARD_SIZE} por lado. */
    public Board() {
        this.grid = new Ship[GameConstants.BOARD_SIZE][GameConstants.BOARD_SIZE];
        this.fleet = new ArrayList<>();
        this.firedShots = new HashSet<>();
    }

    /**
     * Coloca un barco si todas sus casillas caen dentro del tablero y están
     * libres. La operación es atómica: o se coloca completo, o no se coloca
     * nada.
     *
     * @param ship barco a colocar (construido por {@link ShipFactory})
     * @throws InvalidShipPlacementException si alguna casilla queda fuera del
     *                                       tablero o pisa otro barco ya colocado
     */
    public void placeShip(Ship ship) throws InvalidShipPlacementException {
        for (Coordinate cell : ship.getOccupiedCells()) {
            if (!cell.isWithinBoard()) {
                throw new InvalidShipPlacementException(
                        "El barco se sale del tablero en la casilla (" + cell.row() + ", " + cell.column() + ").");
            }
            if (grid[cell.row()][cell.column()] != null) {
                throw new InvalidShipPlacementException(
                        "El barco se solapa con otro ya colocado en (" + cell.row() + ", " + cell.column() + ").");
            }
        }
        for (Coordinate cell : ship.getOccupiedCells()) {
            grid[cell.row()][cell.column()] = ship;
        }
        fleet.add(ship);
    }

    /**
     * Resuelve un disparo sobre una casilla y lo clasifica.
     *
     * @param target casilla objetivo del disparo
     * @return {@link ShotResult#WATER}, {@link ShotResult#HIT} o {@link ShotResult#SUNK}
     * @throws OutOfBoundsShotException si la casilla está fuera del tablero
     */
    public ShotResult receiveShot(Coordinate target) throws OutOfBoundsShotException {
        if (!target.isWithinBoard()) {
            throw new OutOfBoundsShotException(
                    "Disparo fuera del tablero: (" + target.row() + ", " + target.column() + ").");
        }
        firedShots.add(target);
        Ship ship = grid[target.row()][target.column()];
        if (ship == null) {
            return ShotResult.WATER;
        }
        ship.registerHit(target);
        return ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
    }

    /**
     * @param coordinate casilla a consultar
     * @return el barco que ocupa esa casilla, o {@code null} si es agua
     */
    public Ship shipAt(Coordinate coordinate) {
        if (!coordinate.isWithinBoard()) {
            return null;
        }
        return grid[coordinate.row()][coordinate.column()];
    }

    /**
     * @param coordinate casilla a consultar
     * @return {@code true} si esa casilla ya fue objeto de un disparo
     */
    public boolean hasBeenShot(Coordinate coordinate) {
        return firedShots.contains(coordinate);
    }

    /**
     * @return {@code true} si hay al menos un barco y todos están hundidos
     *         (condición de fin de partida)
     */
    public boolean areAllShipsSunk() {
        if (fleet.isEmpty()) {
            return false;
        }
        for (Ship ship : fleet) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return las colocaciones de la flota, para que la vista las dibuje o la
     *         persistencia las guarde (lista de solo lectura)
     */
    public List<ShipPlacement> getFleetPlacements() {
        List<ShipPlacement> placements = new ArrayList<>();
        for (Ship ship : fleet) {
            placements.add(ship.getPlacement());
        }
        return List.copyOf(placements);
    }

    /**
     * @return las casillas ya disparadas sobre este tablero (lista de solo
     *         lectura); sirve para capturar y restaurar el estado de la partida
     */
    public List<Coordinate> getFiredShots() {
        return List.copyOf(firedShots);
    }
}
