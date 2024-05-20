package com.example.cyberlarpapi.game.exceptions.FactionException;

public class FactionServiceException extends FactionException{
    public FactionServiceException(String message) {
        super(message);
    }

    public FactionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
