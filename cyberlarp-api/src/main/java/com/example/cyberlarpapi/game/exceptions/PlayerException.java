package com.example.cyberlarpapi.game.exceptions;

public class PlayerException extends GameException{
    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
