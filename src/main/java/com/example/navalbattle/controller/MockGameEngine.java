package com.example.navalbattle.controller;

import com.example.navalbattle.exceptions.OutOfBoundsShotException;
import com.example.navalbattle.model.*;
import com.example.navalbattle.model.ShipPlacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Motor de partida TEMPORAL, solo para poder probar visualmente la
 * vista de combate mientras el equipo construye el motor real: coloca
 * al azar la flota del jugador y la de la IA, resuelve disparos, y hace
 * que la IA dispare a celdas al azar que no haya intentado antes.
 * <p>
 * // TODO: REEMPLAZAR por la implementación real del compañero de
 * modelo/IA antes de la entrega. Este motor no valida separación entre
 * barcos ni implementa ninguna estrategia real de IA: solo existe para
 * que {@link com.example.navalbattle.controller.GameController} tenga
 * algo funcional contra qué probarse mientras tanto.
 *
 * @author Jorge Navia
 * @version 1.0
 */
final class MockGameEngine implements GameEngine {

    private static final int BOARD_SIZE = 10;
    private static final int MAX_PLACEMENT_ATTEMPTS = 200;

    private record Coord(int row, int column) {
    }

    private final Random random = new Random();

    private final List<ShipPlacement> playerFleet = new ArrayList<>();
    private final Map<Coord, ShipPlacement> playerCellOwner = new HashMap<>();
    private final Map<ShipPlacement, Set<Coord>> playerHits = new HashMap<>();
    private final Set<Coord> aiShotsAtPlayer = new HashSet<>();
    private int playerCellsRemaining;

    private final List<ShipPlacement> aiFleet = new ArrayList<>();
    private final Map<Coord, ShipPlacement> aiCellOwner = new HashMap<>();
    private final Map<ShipPlacement, Set<Coord>> aiHits = new HashMap<>();
    private final Set<Coord> playerShotsAtAi = new HashSet<>();
    private int aiCellsRemaining;

    private boolean playerTurn = true;

    /** Coloca al azar (sin validar separación) la flota de ambos bandos. */
    MockGameEngine() {
        placeFleetRandomly(playerFleet, playerCellOwner, playerHits);
        placeFleetRandomly(aiFleet, aiCellOwner, aiHits);
        playerCellsRemaining = totalFleetCells();
        aiCellsRemaining = totalFleetCells();
    }

    /**
     * Usa la flota que el jugador realmente colocó en
     * {@code BoardSetupController} en vez de generarle una al azar; la
     * IA sigue recibiendo una flota aleatoria (oculta, como corresponde).
     *
     * @param realPlayerFleet flota que el jugador colocó en la grilla
     */
    MockGameEngine(List<ShipPlacement> realPlayerFleet) {
        for (ShipPlacement placement : realPlayerFleet) {
            for (Coord cell : computeCells(placement.type(), placement.startRow(), placement.startColumn(), placement.orientation())) {
                playerCellOwner.put(cell, placement);
            }
            playerHits.put(placement, new HashSet<>());
            playerFleet.add(placement);
        }
        placeFleetRandomly(aiFleet, aiCellOwner, aiHits);
        playerCellsRemaining = totalFleetCells();
        aiCellsRemaining = totalFleetCells();
    }

    private int totalFleetCells() {
        int total = 0;
        for (ShipType type : ShipType.values()) {
            total += type.getSizeInCells() * type.getFleetCount();
        }
        return total;
    }

    private void placeFleetRandomly(List<ShipPlacement> fleet, Map<Coord, ShipPlacement> cellOwner,
                                     Map<ShipPlacement, Set<Coord>> hits) {
        for (ShipType type : ShipType.values()) {
            for (int unit = 0; unit < type.getFleetCount(); unit++) {
                placeOneShipRandomly(type, fleet, cellOwner, hits);
            }
        }
    }

    private void placeOneShipRandomly(ShipType type, List<ShipPlacement> fleet,
                                       Map<Coord, ShipPlacement> cellOwner, Map<ShipPlacement, Set<Coord>> hits) {
        for (int attempt = 0; attempt < MAX_PLACEMENT_ATTEMPTS; attempt++) {
            Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            int startRow = random.nextInt(BOARD_SIZE);
            int startColumn = random.nextInt(BOARD_SIZE);
            List<Coord> cells = computeCells(type, startRow, startColumn, orientation);
            if (isFree(cells, cellOwner)) {
                ShipPlacement placement = new ShipPlacement(type, startRow, startColumn, orientation);
                for (Coord cell : cells) {
                    cellOwner.put(cell, placement);
                }
                hits.put(placement, new HashSet<>());
                fleet.add(placement);
                return;
            }
        }
    }

    private List<Coord> computeCells(ShipType type, int startRow, int startColumn, Orientation orientation) {
        List<Coord> cells = new ArrayList<>();
        boolean horizontal = orientation == Orientation.HORIZONTAL;
        for (int offset = 0; offset < type.getSizeInCells(); offset++) {
            int row = horizontal ? startRow : startRow + offset;
            int column = horizontal ? startColumn + offset : startColumn;
            cells.add(new Coord(row, column));
        }
        return cells;
    }

    private boolean isFree(List<Coord> cells, Map<Coord, ShipPlacement> cellOwner) {
        for (Coord cell : cells) {
            if (cell.row() < 0 || cell.row() >= BOARD_SIZE || cell.column() < 0 || cell.column() >= BOARD_SIZE) {
                return false;
            }
            if (cellOwner.containsKey(cell)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    @Override
    public ShotOutcome shootOpponent(int row, int column) throws OutOfBoundsShotException {
        validateBounds(row, column);
        Coord target = new Coord(row, column);
        if (playerShotsAtAi.contains(target)) {
            return new ShotOutcome(ShotResult.WATER, null);
        }
        playerShotsAtAi.add(target);

        ShotOutcome outcome = resolveShot(target, aiCellOwner, aiHits);
        if (outcome.result() != ShotResult.WATER) {
            aiCellsRemaining--;
        }
        playerTurn = false;
        return outcome;
    }

    @Override
    public AiShotOutcome playAiTurn() {
        Coord target = pickAiTarget();
        aiShotsAtPlayer.add(target);

        ShotOutcome outcome = resolveShot(target, playerCellOwner, playerHits);
        if (outcome.result() != ShotResult.WATER) {
            playerCellsRemaining--;
        }
        playerTurn = true;
        return new AiShotOutcome(target.row(), target.column(), outcome);
    }

    private Coord pickAiTarget() {
        Coord target;
        do {
            target = new Coord(random.nextInt(BOARD_SIZE), random.nextInt(BOARD_SIZE));
        } while (aiShotsAtPlayer.contains(target));
        return target;
    }

    private ShotOutcome resolveShot(Coord target, Map<Coord, ShipPlacement> cellOwner,
                                     Map<ShipPlacement, Set<Coord>> hits) {
        ShipPlacement hitShip = cellOwner.get(target);
        if (hitShip == null) {
            return new ShotOutcome(ShotResult.WATER, null);
        }
        Set<Coord> shipHits = hits.get(hitShip);
        shipHits.add(target);
        boolean sunk = shipHits.size() == hitShip.type().getSizeInCells();
        return sunk ? new ShotOutcome(ShotResult.SUNK, hitShip) : new ShotOutcome(ShotResult.HIT, null);
    }

    private void validateBounds(int row, int column) throws OutOfBoundsShotException {
        if (row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) {
            throw new OutOfBoundsShotException("La coordenada (" + row + ", " + column + ") está fuera del tablero.");
        }
    }

    @Override
    public boolean isGameOver() {
        return playerCellsRemaining <= 0 || aiCellsRemaining <= 0;
    }

    @Override
    public boolean didPlayerWin() {
        return aiCellsRemaining <= 0;
    }

    @Override
    public List<ShipPlacement> getPlayerFleet() {
        return List.copyOf(playerFleet);
    }

    @Override
    public List<Coordinate> getPlayerShots() {
        return List.of();
    }

    @Override
    public List<Coordinate> getAiShots() {
        return List.of();
    }

    @Override
    public Board getPlayerBoard() {
        return null;
    }

    @Override
    public Board getOpponentBoard() {
        return null;
    }
}
