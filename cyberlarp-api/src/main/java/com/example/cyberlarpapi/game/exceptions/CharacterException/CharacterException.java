package com.example.cyberlarpapi.game.exceptions.CharacterException;

import com.example.cyberlarpapi.game.exceptions.GameException.GameException;

public class CharacterException extends Exception {
    public CharacterException(String message) {
        super(message);
    }

    public CharacterException(String message, Throwable cause) {
        super(message, cause);
    }
}
