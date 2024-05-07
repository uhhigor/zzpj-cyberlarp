package com.example.cyberlarpapi;

import java.util.Optional;
import com.example.cyberlarpapi.game.data.Game;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
