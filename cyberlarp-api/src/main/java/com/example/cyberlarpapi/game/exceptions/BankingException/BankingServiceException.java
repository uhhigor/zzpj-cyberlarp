package com.example.cyberlarpapi.game.exceptions.BankingException;

public class BankingServiceException extends Exception {
    public BankingServiceException(String message) {
        super(message);
    }

    public BankingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
