package com.example.cyberlarpapi;

import com.example.cyberlarpapi.game.data.Game;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
