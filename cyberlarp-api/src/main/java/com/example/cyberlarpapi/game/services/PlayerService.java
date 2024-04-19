package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.data.Game;
import com.example.cyberlarpapi.game.data.Player;
import com.example.cyberlarpapi.game.data.PlayerDTO;
import com.example.cyberlarpapi.game.data.character.faction.Faction;
import com.example.cyberlarpapi.game.data.character.faction.FactionDTO;
import com.example.cyberlarpapi.game.data.character.style.Style;
import com.example.cyberlarpapi.game.data.character.style.StyleDTO;
import com.example.cyberlarpapi.game.exceptions.*;
import com.example.cyberlarpapi.game.repositories.PlayerRepository;
import com.example.cyberlarpapi.game.repositories.character.FactionRepository;
import com.example.cyberlarpapi.game.repositories.character.StyleRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    private final UserService userService;

    private final GameService gameService;


    public PlayerService(PlayerRepository playerRepository, UserService userService, GameService gameService) {
        this.playerRepository = playerRepository;
        this.userService = userService;
        this.gameService = gameService;
    }

    public Player getById(int id) throws PlayerServiceException {
        return playerRepository.findById(id).orElseThrow(() -> new PlayerServiceException("Player not found"));
    }

    public void deleteById(int id) {
        playerRepository.deleteById(id);
    }

    public Player create(PlayerDTO playerDTO) throws PlayerServiceException {
        try {
            User user = userService.getUserById(playerDTO.getUserId());
            Game game = gameService.getById(playerDTO.getGameId());
            Player player = playerRepository.save(Player.builder()
                    .user(user)
                    .game(game)
                    .build()); // Create player

            user.addPlayer(player); // Add player to user
            game.addPlayer(player); // Add player to game
            gameService.update(game); // Update game
            userService.update(user); // Update user
            return player;
        } catch (PlayerException | UserServiceException | GameServiceException e) {
            throw new PlayerServiceException("Error while creating player", e);
        }
    }

    public Player update(int id, PlayerDTO playerDTO) throws PlayerServiceException {
        try {
            Player player = getById(id);
            player.setGame(gameService.getById(playerDTO.getGameId()));
            player.setUser(userService.getUserById(playerDTO.getUserId()));
            return playerRepository.save(player);
        } catch (GameServiceException | UserServiceException e) {
            throw new PlayerServiceException("Error while updating player", e);
        }
    }

    public Player update(Player player) {
        return playerRepository.save(player);
    }

}
