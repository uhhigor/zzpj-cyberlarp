package com.example.cyberlarpapi.game.exceptions;

public class GameServiceException extends GameException{
    public GameServiceException(String message) {
        super(message);
    }

    public GameServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
