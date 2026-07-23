package com.example.navalbattle.model;

import com.example.navalbattle.exceptions.GameStateCorruptedException;
import com.example.navalbattle.exceptions.InvalidShipPlacementException;
import com.example.navalbattle.exceptions.OutOfBoundsShotException;

import java.util.List;
import java.util.Random;

/**
 * Motor de juego real: implementación de {@link GameEngine} que reemplaza al
 * motor de prueba temporal ({@code controller.MockGameEngine}). Une el
 * {@link Board} de cada bando, la fábrica de barcos ({@link ShipFactory},
 * Factory Method) y la estrategia de disparo de la IA ({@link ShotStrategy},
 * Strategy).
 * <p>
 * <b>Reglas de turno (fieles al enunciado):</b> un disparo al agua cede el
 * turno al oponente; un disparo que toca o hunde deja al mismo bando disparar
 * de nuevo. Esta regla aplica por igual al jugador humano y a la máquina.
 * <p>
 * ⚠️ IMPACTO AL EQUIPO (capa controller/vista): esta clase deja el estado de
 * turno correcto en {@link #isPlayerTurn()}, pero para que la IA <i>encadene</i>
 * varios disparos al tocar, el controlador debe repetir la llamada a
 * {@link #playAiTurn()} mientras {@code !isPlayerTurn() && !isGameOver()}. El
 * modelo no puede animar esa secuencia por sí mismo. Además, el reemplazo del
 * motor de prueba se hace inyectando esta clase donde hoy se instancia
 * {@code MockGameEngine}; ninguno de esos dos cambios es de la capa de modelo.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public class DefaultGameEngine implements GameEngine {

    /** Tope de reintentos al colocar un barco de la IA en una casilla al azar. */
    private static final int MAX_PLACEMENT_ATTEMPTS = 200;

    private final Board playerBoard;
    private final Board aiBoard;
    private final ShotStrategy aiStrategy;
    private final ShipFactory shipFactory;
    private final Random random = new Random();

    private boolean playerTurn = true;

    /**
     * Crea el motor con la flota que el jugador colocó realmente en la vista de
     * configuración; a la IA se le genera una flota oculta al azar. Usa la
     * estrategia de disparo aleatoria por defecto.
     *
     * @param playerFleet colocaciones de la flota del jugador
     */
    public DefaultGameEngine(List<ShipPlacement> playerFleet) {
        this(playerFleet, new RandomShotStrategy());
    }

    /**
     * Crea el motor permitiendo inyectar la estrategia de disparo de la IA
     * (útil para cambiar la dificultad o para pruebas deterministas).
     *
     * @param playerFleet colocaciones de la flota del jugador
     * @param aiStrategy  estrategia con la que la IA elige sus blancos
     */
    public DefaultGameEngine(List<ShipPlacement> playerFleet, ShotStrategy aiStrategy) {
        this.shipFactory = new ShipFactory();
        this.aiStrategy = aiStrategy;
        this.playerBoard = buildPlayerBoard(playerFleet);
        this.aiBoard = buildRandomAiBoard();
    }

    /**
     * Reconstruye el tablero del jugador a partir de las colocaciones que ya
     * validó la vista de configuración.
     *
     * @param playerFleet colocaciones de la flota del jugador
     * @return el tablero del jugador con su flota colocada
     * @throws GameStateCorruptedException si una colocación llega inválida, lo
     *                                     que indicaría una ruptura del contrato
     *                                     con la vista (no una entrada del usuario)
     */
    private Board buildPlayerBoard(List<ShipPlacement> playerFleet) {
        Board board = new Board();
        for (ShipPlacement placement : playerFleet) {
            try {
                board.placeShip(shipFactory.create(placement));
            } catch (InvalidShipPlacementException exception) {
                throw new GameStateCorruptedException(
                        "La flota del jugador llegó con una colocación inválida al motor.", exception);
            }
        }
        return board;
    }

    /**
     * Genera el tablero de la IA colocando su flota completa al azar, respetando
     * los tamaños y cantidades de {@link ShipType} y sin solapamientos.
     *
     * @return el tablero de la IA con su flota oculta ya colocada
     */
    private Board buildRandomAiBoard() {
        Board board = new Board();
        for (ShipType type : ShipType.values()) {
            for (int unit = 0; unit < type.getFleetCount(); unit++) {
                placeOneShipRandomly(board, type);
            }
        }
        return board;
    }

    /**
     * Intenta colocar un barco del tipo dado en una posición y orientación al
     * azar, reintentando hasta encontrar un hueco válido.
     *
     * @param board tablero donde colocar el barco
     * @param type  tipo de barco a colocar
     * @throws GameStateCorruptedException si no logra colocarlo tras
     *                                     {@link #MAX_PLACEMENT_ATTEMPTS} intentos
     */
    private void placeOneShipRandomly(Board board, ShipType type) {
        for (int attempt = 0; attempt < MAX_PLACEMENT_ATTEMPTS; attempt++) {
            Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            int startRow = random.nextInt(GameConstants.BOARD_SIZE);
            int startColumn = random.nextInt(GameConstants.BOARD_SIZE);
            ShipPlacement placement = new ShipPlacement(type, startRow, startColumn, orientation);
            try {
                board.placeShip(shipFactory.create(placement));
                return;
            } catch (InvalidShipPlacementException retryable) {
                // Colisión o salida del tablero: se reintenta con otra posición al azar.
            }
        }
        throw new GameStateCorruptedException(
                "No se pudo colocar la flota de la IA tras " + MAX_PLACEMENT_ATTEMPTS + " intentos.");
    }

    @Override
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    @Override
    public ShotOutcome shootOpponent(int row, int column) throws OutOfBoundsShotException {
        Coordinate target = new Coordinate(row, column);
        ShotResult result = aiBoard.receiveShot(target);
        // Regla del enunciado: solo el agua cede el turno; al tocar/hundir se repite.
        if (result == ShotResult.WATER) {
            playerTurn = false;
        }
        return toOutcome(aiBoard, target, result);
    }

    @Override
    public AiShotOutcome playAiTurn() {
        Coordinate target = aiStrategy.chooseTarget(playerBoard);
        ShotResult result = resolveAiShot(target);
        // Misma regla para la IA: solo cede el turno al fallar.
        if (result == ShotResult.WATER) {
            playerTurn = true;
        }
        return new AiShotOutcome(target.row(), target.column(), toOutcome(playerBoard, target, result));
    }

    /**
     * Resuelve el disparo de la IA sobre el tablero del jugador.
     *
     * @param target casilla elegida por la estrategia
     * @return resultado del disparo
     * @throws GameStateCorruptedException si la estrategia devolvió una casilla
     *                                     fuera del tablero (rompe su contrato)
     */
    private ShotResult resolveAiShot(Coordinate target) {
        try {
            return playerBoard.receiveShot(target);
        } catch (OutOfBoundsShotException exception) {
            throw new GameStateCorruptedException(
                    "La estrategia de la IA eligió una casilla fuera del tablero.", exception);
        }
    }

    /**
     * Arma el resultado completo de un disparo: cuando hunde, adjunta la
     * colocación del barco hundido para que la vista revele su silueta.
     *
     * @param board  tablero donde ocurrió el disparo
     * @param target casilla disparada
     * @param result clasificación del disparo
     * @return el resultado con la colocación del barco hundido, o {@code null}
     *         en ese campo si no fue hundimiento
     */
    private ShotOutcome toOutcome(Board board, Coordinate target, ShotResult result) {
        if (result == ShotResult.SUNK) {
            Ship sunkShip = board.shipAt(target);
            return new ShotOutcome(ShotResult.SUNK, sunkShip.getPlacement());
        }
        return new ShotOutcome(result, null);
    }

    @Override
    public boolean isGameOver() {
        return playerBoard.areAllShipsSunk() || aiBoard.areAllShipsSunk();
    }

    @Override
    public boolean didPlayerWin() {
        return aiBoard.areAllShipsSunk();
    }

    @Override
    public List<ShipPlacement> getPlayerFleet() {
        return playerBoard.getFleetPlacements();
    }
}
