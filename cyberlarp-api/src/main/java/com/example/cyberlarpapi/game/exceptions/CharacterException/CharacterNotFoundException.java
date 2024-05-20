package com.example.cyberlarpapi.game.exceptions.CharacterException;

public class CharacterNotFoundException extends CharacterException{
    public CharacterNotFoundException(String message) {
        super(message);
    }

    public CharacterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
