package com.example.cyberlarpapi.game.exceptions.CharacterException;

public class CharacterServiceException extends CharacterException{
    public CharacterServiceException(String message) {
        super(message);
    }

    public CharacterServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
