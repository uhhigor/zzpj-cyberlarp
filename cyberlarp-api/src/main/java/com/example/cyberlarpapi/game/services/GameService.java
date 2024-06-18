package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.model.chat.SCOPE;
import com.example.cyberlarpapi.game.model.chat.message.Message;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.repositories.game.GameRepository;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final CharacterRepository characterRepository;

    private final CharacterService characterService;

    public GameService(GameRepository gameRepository, CharacterService characterService, CharacterRepository characterRepository) {
        this.gameRepository = gameRepository;
        this.characterService = characterService;
        this.characterRepository = characterRepository;
    }

    public void addCharacterToGame(Integer gameId, Character character) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (!game.getCharacters().contains(character)){
                    game.addCharacter(character);
                    gameRepository.save(game);
                }
            }
        }
    }

    public void kickCharacterFromGame(Integer gameId, Character character) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (game.getCharacters().contains(character)) {
                    game.removeCharacter(character);
                    gameRepository.save(game);
                }
            }
        }
    }

    public boolean makeUserOwnerOfGame(Integer gameId, _User user) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (game.getGameMaster() != user) {
                    game.setGameMaster(user);
                    gameRepository.save(game);
                    return true;
                }
            }
        }
        return false;
    }

    public void addMessageToGame(Integer gameId, Message message) {
        try {
            for (Game game : this.gameRepository.findAll()) {
                if (game.getId().equals(gameId)) {
                    game.addMessage(message);
                    gameRepository.save(game);
                }
            }
        } catch (Exception e) {
            throw new InvalidFactionException("Invalid faction");
        }
    }

    public void deleteMessageFromGame(Integer gameId, Message message) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                game.deleteMessage(message);
                gameRepository.save(game);
            }
        }
    }

    public List<Message> getMessagesFromGame(Integer gameId, Character character, SCOPE scope) {
        try {
            for (Game game : this.gameRepository.findAll()) {
                if (game.getId().equals(gameId)) {
                    return game.getMessages(character, scope);
                }
            }
        } catch (Exception e) {
            throw new InvalidFactionException("Invalid faction");
        }
        return null;
    }

    public boolean startGame() {
        return false;
    }

    public List<Game> getAll() {
        return StreamUtils.createStreamFromIterator(gameRepository.findAll().iterator()).toList();
    }

    public Game getById(int id) throws GameNotFoundException {
        return gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException("Game " + id + " not found"));
    }

    public void deleteById(int id) throws GameServiceException {
        if(!gameRepository.existsById(id))
            throw new GameServiceException("Game " + id + " not found");
        gameRepository.deleteById(id);
    }

    public Game save(Game game) {

        return gameRepository.save(game);
    }
}
