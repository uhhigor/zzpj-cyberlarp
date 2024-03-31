package com.example.cyberlarpapi.exceptions;

public class CharacterException extends Exception{
    public CharacterException(String message, Throwable cause) {
        super(message, cause);
    }
    public CharacterException(String message) {
        super(message);
    }
}
