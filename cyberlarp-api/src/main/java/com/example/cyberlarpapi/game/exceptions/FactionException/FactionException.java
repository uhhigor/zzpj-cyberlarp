package com.example.cyberlarpapi.game.exceptions.FactionException;

import com.example.cyberlarpapi.game.exceptions.GameException.GameException;

public class FactionException extends GameException {
    public FactionException(String message) {
        super(message);
    }

    public FactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
