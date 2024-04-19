package com.example.cyberlarpapi.game.exceptions.CharacterClassException;

import com.example.cyberlarpapi.game.exceptions.GameException.GameException;

public class CharacterClassException extends GameException {

    public CharacterClassException(String message) {
        super(message);
    }

    public CharacterClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
