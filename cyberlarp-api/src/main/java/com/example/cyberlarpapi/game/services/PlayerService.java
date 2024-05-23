package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerNotFoundException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getById(int id) throws PlayerNotFoundException {
        return playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException("Player " + id + " not found"));
    }

    public void deleteById(int id) throws PlayerNotFoundException {
        if(!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException("Player " + id + " not found");
        }
        playerRepository.deleteById(id);
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public Player update(Player player) {
        return playerRepository.save(player);
    }

}
