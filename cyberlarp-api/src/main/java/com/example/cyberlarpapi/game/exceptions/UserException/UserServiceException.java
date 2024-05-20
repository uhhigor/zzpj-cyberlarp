package com.example.cyberlarpapi.game.exceptions.UserException;

public class UserServiceException extends UserException{
    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
