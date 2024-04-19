package com.example.cyberlarpapi.game.exceptions;

public class PlayerServiceException extends PlayerException{
    public PlayerServiceException(String message) {
        super(message);
    }

    public PlayerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
