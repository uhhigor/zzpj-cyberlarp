package com.example.cyberlarpapi.game.exceptions.PlayerException;

public class PlayerNotFoundException extends PlayerException{
    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
