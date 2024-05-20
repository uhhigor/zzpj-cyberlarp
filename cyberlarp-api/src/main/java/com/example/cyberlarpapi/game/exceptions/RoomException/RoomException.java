package com.example.cyberlarpapi.game.exceptions.RoomException;

import com.example.cyberlarpapi.game.exceptions.GameException.GameException;

public class RoomException extends GameException {

    public RoomException(String message) {
        super(message);
    }

    public RoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
