package com.example.cyberlarpapi.game.repositories.game;

import com.example.cyberlarpapi.game.model.game.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {
}
