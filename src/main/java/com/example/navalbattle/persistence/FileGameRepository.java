package com.example.navalbattle.persistence;

import com.example.navalbattle.exceptions.GameStateCorruptedException;
import com.example.navalbattle.model.GameSnapshot;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class FileGameRepository implements GameRepository{

    private static final String SAVE_FILE = "last-game.dat";

    @Override
    public boolean hasSavedGame() {
        return new File(SAVE_FILE).exists();
    }

    @Override
    public GameSnapshot loadLastGame() throws GameStateCorruptedException {
        try (ObjectInputStream input =
                     new ObjectInputStream(new FileInputStream(SAVE_FILE))) {

            return (GameSnapshot) input.readObject();

        } catch (IOException | ClassNotFoundException exception) {
            throw new GameStateCorruptedException(
                    "No se pudo cargar la partida guardada.",
                    exception
            );
        }
    }

    @Override
    public void save(GameSnapshot snapshot) {
        try (ObjectOutputStream output =
                     new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {

            output.writeObject(snapshot);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
