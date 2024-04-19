package com.example.cyberlarpapi.game.exceptions;

public class CharacterException extends GameException {
    public CharacterException(String message) {
        super(message);
    }

    public CharacterException(String message, Throwable cause) {
        super(message, cause);
    }
}
