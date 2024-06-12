package com.example.cyberlarpapi.game.exceptions.ChatExceptions;

public class CharacterAlreadyInGroupException extends RuntimeException {
    public CharacterAlreadyInGroupException(String message) {
        super(message);
    }
}