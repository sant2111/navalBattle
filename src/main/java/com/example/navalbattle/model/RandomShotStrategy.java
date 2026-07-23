package com.example.navalbattle.model;

import java.util.Random;

/**
 * Estrategia de disparo aleatoria: la IA elige una casilla al azar que aún no
 * haya disparado. Es la implementación por defecto de {@link ShotStrategy} y
 * reproduce el comportamiento del motor de prueba temporal.
 * <p>
 * Al depender solo de {@link Board#hasBeenShot(Coordinate)}, no "ve" dónde
 * están los barcos: dispara a ciegas, como corresponde a una IA básica.
 *
 * @author Santiago Barragan
 * @version 1.0
 */
public class RandomShotStrategy implements ShotStrategy {

    private final Random random = new Random();

    /**
     * {@inheritDoc}
     * <p>
     * Sortea casillas hasta encontrar una no disparada. El bucle termina
     * siempre porque la partida acaba antes de que se agoten las casillas
     * libres (al hundirse la última flota).
     */
    @Override
    public Coordinate chooseTarget(Board opponentBoard) {
        Coordinate target;
        do {
            int row = random.nextInt(GameConstants.BOARD_SIZE);
            int column = random.nextInt(GameConstants.BOARD_SIZE);
            target = new Coordinate(row, column);
        } while (opponentBoard.hasBeenShot(target));
        return target;
    }
}
