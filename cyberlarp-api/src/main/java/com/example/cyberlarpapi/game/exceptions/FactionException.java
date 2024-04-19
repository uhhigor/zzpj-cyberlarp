package com.example.cyberlarpapi.game.exceptions;

public class FactionException extends GameException{
    public FactionException(String message) {
        super(message);
    }

    public FactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
