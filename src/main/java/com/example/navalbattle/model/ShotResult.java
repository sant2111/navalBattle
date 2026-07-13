package com.example.navalbattle.model;

/**
 * Resultado posible de un disparo.
 * <p>
 * // TODO: confirma compañero de modelo — nombres y semántica exactos.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public enum ShotResult {

    /** El disparo no impactó ningún barco. */
    WATER,

    /** El disparo impactó parte de un barco, que aún no queda hundido. */
    HIT,

    /** El disparo impactó la última casilla libre de un barco: queda hundido. */
    SUNK
}
