package com.example.cyberlarpapi.game.exceptions.PlayerException;

public class PlayerServiceException extends PlayerException{
    public PlayerServiceException(String message) {
        super(message);
    }

    public PlayerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
