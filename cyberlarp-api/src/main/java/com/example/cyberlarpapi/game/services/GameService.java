package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.User;
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

    public void addPlayerToGame(Integer gameId, Player player) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (!game.getPlayers().contains(player)){
                    game.addPlayer(player);
                    gameRepository.save(game);
                }
            }
        }
    }

    public void kickPlayerFromGame(Integer gameId, Player player) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (game.getPlayers().contains(player)) {
                    game.removePlayer(player);
                    gameRepository.save(game);
                }
            }
        }
    }

    public boolean makeUserOwnerOfGame(Integer gameId, User user) {
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

    // ====================== Banking ========================== //

    public void addTransaction(Transaction transaction, int id) throws GameServiceException {
        if(!gameRepository.existsById(id))
            throw new GameServiceException("Game " + id + " not found");
        Optional<Game> game = gameRepository.findById(id);
        game.ifPresent(value -> value.getTransactions().add(transaction));
    }

    public List<Transaction> getTransactions(String characterBankNumber, int id) throws GameServiceException, GameNotFoundException, BankingServiceException {
        if(!gameRepository.existsById(id))
            throw new GameNotFoundException("Game " + id + " not found");
        Character character = characterRepository.findByAccountNumber(characterBankNumber);
        if (character == null) {
            throw new BankingServiceException("There is no character with given account number!");
        }
        if (character.getCharacterClass() == CharacterClass.NETRUNNER) {
            Optional<Game> game = gameRepository.findById(id);
            if (game.isPresent()) {
                return game.get().getTransactions();
            }
        } else {
            throw new GameServiceException("Character does not have access to transactions");
        }
        return null;
    }
}
