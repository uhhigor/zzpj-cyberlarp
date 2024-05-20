package com.example.cyberlarpapi.game.exceptions.GameException;

public class GameNotFoundException extends GameException{
    public GameNotFoundException(String message) {
        super(message);
    }

    public GameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
