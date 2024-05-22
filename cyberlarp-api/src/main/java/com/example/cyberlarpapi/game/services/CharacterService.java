package com.example.cyberlarpapi.game.services;

import java.util.List;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    private final PlayerService playerService;

    public CharacterService(CharacterRepository characterRepository, PlayerService playerService) {
        this.characterRepository = characterRepository;
        this.playerService = playerService;
    }


    public Character save(Character character) {
        return characterRepository.save(character);
    }

    public Character getById(int id) throws CharacterNotFoundException {
        return characterRepository.findById(id).orElseThrow(() -> new CharacterNotFoundException("Character with id " + id + " not found"));
    }

    public void deleteById(int id) throws CharacterNotFoundException {
        if(!characterRepository.existsById(id)){
            throw new CharacterNotFoundException("Character with id " + id + " not found");
        }
        characterRepository.deleteById(id);
    }

    public Character getCharacterByUserId(int userId) throws CharacterServiceException {
        try {
            return characterRepository.findByUserId(userId).orElseThrow(() -> new CharacterServiceException("Character not found"));
        } catch (Exception e) {
            throw new CharacterServiceException("Error while getting character by user id", e);
        }
    }

    public List<Character> getCharactersByUserId(int userId) throws CharacterServiceException {
        try {
            List<Character> characters = characterRepository.findAllByUserId(userId);
            if (characters.isEmpty()) {
                throw new CharacterServiceException("No characters found for user id: " + userId);
            }
            return characters;
        } catch (Exception e) {
            throw new CharacterServiceException("Error while getting characters by user id", e);
        }
    }


}
