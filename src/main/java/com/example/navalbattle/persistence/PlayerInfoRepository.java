package com.example.navalbattle.persistence;

import java.io.*;

/**
 * Gestiona la persistencia de la información básica del jugador en un
 * archivo de texto plano.
 * <p>
 * Actualmente almacena el nickname del jugador y la cantidad de barcos
 * enemigos hundidos durante la partida, permitiendo que esta información
 * se conserve entre ejecuciones de la aplicación.
 *
 * @author Carlos Meneses
 * @version 1.0
 */
public class PlayerInfoRepository {

    /**
     * Nombre del archivo donde se almacena la información del jugador.
     */
    private static final String FILE_NAME = "player-info.txt";

    /**
     * Guarda la información del jugador en el archivo de persistencia.
     * Si el archivo ya existe, su contenido será reemplazado.
     *
     * @param nickname nombre del jugador
     * @param shipsSunk cantidad de barcos enemigos hundidos
     */
    public void save(String nickname, int shipsSunk) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write("Nickname=" + nickname);
            writer.newLine();
            writer.write("ShipsSunk=" + shipsSunk);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Obtiene la cantidad de barcos enemigos hundidos almacenada en el
     * archivo de persistencia.
     *
     * @return número de barcos hundidos registrados; devuelve {@code 0} si
     *         ocurre un error durante la lectura o si la información no
     *         está disponible
     */
    public int getShipsSunk() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            reader.readLine(); // Nickname=...

            String line = reader.readLine(); // ShipsSunk=0

            if (line != null && line.startsWith("ShipsSunk=")) {
                return Integer.parseInt(line.substring("ShipsSunk=".length()));
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return 0;
    }

}