package com.example.cyberlarpapi.game.exceptions.FactionException;

public class FactionNotFoundException extends FactionException{
    public FactionNotFoundException(String message) {
        super(message);
    }

    public FactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
