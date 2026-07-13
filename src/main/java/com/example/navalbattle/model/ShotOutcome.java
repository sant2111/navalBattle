package com.example.navalbattle.model;

/**
 * Resultado completo de un disparo: el {@link ShotResult} y, si ese
 * disparo hundió un barco, la colocación completa de ese barco para que
 * la vista pueda revelar su silueta en el tablero enemigo.
 * <p>
 * // TODO: confirma compañero de modelo.
 *
 * @param result   resultado del disparo
 * @param sunkShip colocación del barco hundido, o {@code null} si
 *                 {@code result} no es {@link ShotResult#SUNK}
 * @author Jorge Navia
 * @version 1.0
 */
public record ShotOutcome(ShotResult result, ShipPlacement sunkShip) {
}
