package com.example.cyberlarpapi.game.exceptions.UserException;

import com.example.cyberlarpapi.game.exceptions.GameException.GameException;

public class UserException extends GameException {
    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
