package com.example.cyberlarpapi.game.exceptions.GameException;

public class GameException extends Exception{
    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
