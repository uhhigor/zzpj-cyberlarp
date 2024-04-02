package com.example.cyberlarpapi.game.repositories;

import com.example.cyberlarpapi.game.data.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {
}
