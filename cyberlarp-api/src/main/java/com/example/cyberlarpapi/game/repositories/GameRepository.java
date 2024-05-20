package com.example.cyberlarpapi.game.repositories;

import com.example.cyberlarpapi.game.model.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {
}
