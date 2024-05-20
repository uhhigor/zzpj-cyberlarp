package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.player.Player;
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

    public Character setPlayer(Character character, int playerId) throws CharacterServiceException {
        try {

            Player player = playerService.getById(playerId);
            character.setPlayer(player); // Set the character to the player
            player.setCharacter(character); // Set the player to the character
            playerService.update(player); // Update the player
            return characterRepository.save(character); // Update the character
        } catch (Exception e) {
            throw new CharacterServiceException("Error while setting player", e);
        }
    }

}
