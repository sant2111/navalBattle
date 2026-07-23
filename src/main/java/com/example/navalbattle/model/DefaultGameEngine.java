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
 * @author Santiago Barragan
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
        this.playerBoard = buildBoardFrom(playerFleet);
        this.aiBoard = buildRandomAiBoard();
    }

    /**
     * Reconstruye el motor desde una partida guardada, con la estrategia de
     * disparo aleatoria por defecto.
     *
     * @param snapshot estado serializable de la partida a restaurar
     */
    public DefaultGameEngine(GameSnapshot snapshot) {
        this(snapshot, new RandomShotStrategy());
    }

    /**
     * Reconstruye el motor desde una partida guardada, con una estrategia de
     * disparo dada. Rearma ambos tableros desde sus flotas guardadas y vuelve a
     * aplicar los disparos para restaurar el estado de tocado/hundido.
     *
     * @param snapshot   estado serializable de la partida a restaurar
     * @param aiStrategy estrategia con la que la IA elegirá sus blancos
     */
    public DefaultGameEngine(GameSnapshot snapshot, ShotStrategy aiStrategy) {
        this.shipFactory = new ShipFactory();
        this.aiStrategy = aiStrategy;
        this.playerBoard = buildBoardFrom(snapshot.playerFleet());
        this.aiBoard = buildBoardFrom(snapshot.aiFleet());
        replayShots(aiBoard, snapshot.playerShots());
        replayShots(playerBoard, snapshot.aiShots());
        this.playerTurn = snapshot.playerTurn();
    }

    /**
     * Arma un tablero a partir de una lista de colocaciones: sirve tanto para
     * la flota real que colocó el jugador como para cualquiera de las dos
     * flotas al restaurar una partida guardada.
     *
     * @param fleet colocaciones de la flota a colocar
     * @return el tablero con su flota colocada
     * @throws GameStateCorruptedException si una colocación llega inválida, lo
     *                                     que indicaría una ruptura de contrato
     *                                     (no una entrada del usuario)
     */
    private Board buildBoardFrom(List<ShipPlacement> fleet) {
        Board board = new Board();
        for (ShipPlacement placement : fleet) {
            try {
                board.placeShip(shipFactory.create(placement));
            } catch (InvalidShipPlacementException exception) {
                throw new GameStateCorruptedException(
                        "Una colocación llegó inválida al motor de juego.", exception);
            }
        }
        return board;
    }

    /**
     * Vuelve a aplicar sobre un tablero los disparos guardados, para restaurar
     * su estado de tocado/hundido al cargar una partida.
     *
     * @param board tablero sobre el que se realizaron esos disparos
     * @param shots casillas disparadas (el orden no altera el resultado)
     * @throws GameStateCorruptedException si un disparo guardado cae fuera del
     *                                     tablero (partida corrupta)
     */
    private void replayShots(Board board, List<Coordinate> shots) {
        for (Coordinate shot : shots) {
            try {
                board.receiveShot(shot);
            } catch (OutOfBoundsShotException exception) {
                throw new GameStateCorruptedException(
                        "La partida guardada contiene un disparo fuera del tablero.", exception);
            }
        }
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

    @Override
    public List<ShipPlacement> getOpponentFleet() {
        return aiBoard.getFleetPlacements();
    }

    /**
     * Captura el estado actual de la partida como un {@link GameSnapshot}
     * serializable, para que la capa de persistencia lo guarde.
     *
     * @return el estado serializable de la partida en curso
     */
    public GameSnapshot toSnapshot() {
        return new GameSnapshot(
                playerBoard.getFleetPlacements(),
                aiBoard.getFleetPlacements(),
                aiBoard.getFiredShots(),
                playerBoard.getFiredShots(),
                playerTurn);
    }
}
