package com.example.cyberlarpapi.game.services;

import java.util.List;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerNotFoundException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.repositories.TransactionRepository;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import com.example.cyberlarpapi.game.repositories.game.GameRepository;
import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository, TransactionRepository transactionRepository, GameRepository gameRepository) {
        this.characterRepository = characterRepository;
        this.transactionRepository = transactionRepository;
        this.gameRepository = gameRepository;
    }
    private final TransactionRepository transactionRepository;
    private final GameRepository gameRepository;

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
            throw new CharacterServiceException("Error while setting player", e);
        }
    }
}