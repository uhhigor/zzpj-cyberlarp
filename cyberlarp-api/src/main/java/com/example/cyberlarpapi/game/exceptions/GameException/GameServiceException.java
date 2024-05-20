package com.example.cyberlarpapi.game.exceptions.GameException;

public class GameServiceException extends GameException{
    public GameServiceException(String message) {
        super(message);
    }

    public GameServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
