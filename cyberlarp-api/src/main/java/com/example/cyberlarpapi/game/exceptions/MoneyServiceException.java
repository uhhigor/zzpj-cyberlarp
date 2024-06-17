package com.example.cyberlarpapi.game.exceptions;

public class MoneyServiceException extends Exception{
    public MoneyServiceException(String message) {
        super(message);
    }

    public MoneyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
