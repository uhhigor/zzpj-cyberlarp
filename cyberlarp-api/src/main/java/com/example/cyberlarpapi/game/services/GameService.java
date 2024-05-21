package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.repositories.game.GameRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final CharacterService characterService;


    public GameService(GameRepository gameRepository, CharacterService characterService) {
        this.gameRepository = gameRepository;
        this.characterService = characterService;
    }

    public boolean addPlayerToGame(Integer gameId, Player player) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (!game.getPlayers().contains(player)){
                    return game.getPlayers().add(player);
                }
            }
        }
        return false;
    }

    public boolean kickPlayerFromGame(Integer gameId, Player player) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(gameId)) {
                if (game.getPlayers().contains(player)) {
                    return game.getPlayers().remove(player);
                }
            }
        }
        return false;
    }

    public boolean makeUserOwnerOfGame(Integer roomId, User user) {
        for (Game game : this.gameRepository.findAll()) {
            if (game.getId().equals(roomId)) {
                if (game.getGameMaster() != user) {
                    game.setGameMaster(user);
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

    public void updateById(int id, Game game) throws GameServiceException, GameNotFoundException {
        if(!gameRepository.existsById(id))
            throw new GameServiceException("Game " + id + " not found");
        Game oldGame = getById(id);
        oldGame.setGameMaster(game.getGameMaster());
        oldGame.setName(game.getName());
        oldGame.setDescription(game.getDescription());
        oldGame.setPlayers(game.getPlayers());
        oldGame.setAvailableCharacters(game.getAvailableCharacters());
        gameRepository.save(oldGame);
    }

    public Game save(Game game) {

        return gameRepository.save(game);
    }
}
