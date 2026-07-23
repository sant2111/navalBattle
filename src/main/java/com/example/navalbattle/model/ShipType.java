package com.example.navalbattle.model;

/**
 * Flota fija del juego confirmada con el enunciado del miniproyecto: cada
 * tipo de barco define cuántas casillas ocupa y cuántas unidades hay de
 * ese tipo en la flota completa.
 * <p>
 * // TODO: confirma/extiende compañero de modelo — este enum es la fuente
 * de verdad de la composición de la flota (1 portaaviones, 2 submarinos,
 * 3 destructores, 4 fragatas = 10 barcos, 20 casillas). El tablero, la
 * validación de colocación y la IA deben usar exactamente estos mismos
 * tamaños y cantidades.
 * <p>
 * ⚠️ IMPACTO AL EQUIPO: la vista de colocación ({@code BoardSetupController})
 * ya depende de esta composición exacta; cualquier cambio debe acordarse
 * con Jorge antes de modificarla.
 *
 * @author Santiago Barragan
 * @version 1.0
 */
public enum ShipType {

    CARRIER(4, 1, "Portaaviones"),
    SUBMARINE(3, 2, "Submarino"),
    DESTROYER(2, 3, "Destructor"),
    FRIGATE(1, 4, "Fragata");

    private final int sizeInCells;
    private final int fleetCount;
    private final String displayName;

    ShipType(int sizeInCells, int fleetCount, String displayName) {
        this.sizeInCells = sizeInCells;
        this.fleetCount = fleetCount;
        this.displayName = displayName;
    }

    /**
     * @return cantidad de casillas que ocupa un barco de este tipo
     */
    public int getSizeInCells() {
        return sizeInCells;
    }

    /**
     * @return cantidad de unidades de este tipo en la flota completa
     */
    public int getFleetCount() {
        return fleetCount;
    }

    /**
     * @return nombre legible del tipo de barco, en español
     */
    public String getDisplayName() {
        return displayName;
    }
}
