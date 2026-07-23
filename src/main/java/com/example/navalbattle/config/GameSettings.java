package com.example.navalbattle.config;

public final class GameSettings {

    private GameSettings() {
    }

    private static boolean verifyOpponentBoard = false;

    public static boolean isVerifyOpponentBoard() {
        return verifyOpponentBoard;
    }

    public static void setVerifyOpponentBoard(boolean value) {
        verifyOpponentBoard = value;
    }
}