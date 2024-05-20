package com.example.cyberlarpapi.game.repositories;

import com.example.cyberlarpapi.game.model.player.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
}
