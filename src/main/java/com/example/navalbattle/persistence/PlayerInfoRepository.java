package com.example.navalbattle.persistence;

import java.io.*;

public class PlayerInfoRepository {

    private static final String FILE_NAME = "player-info.txt";

    public void save(String nickname, int shipsSunk) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write("Nickname=" + nickname);
            writer.newLine();
            writer.write("ShipsSunk=" + shipsSunk);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

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