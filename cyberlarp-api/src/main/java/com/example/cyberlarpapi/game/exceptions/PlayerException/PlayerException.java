package com.example.cyberlarpapi.game.exceptions.PlayerException;

import com.example.cyberlarpapi.game.exceptions.GameException.GameException;

public class PlayerException extends Exception {
    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
