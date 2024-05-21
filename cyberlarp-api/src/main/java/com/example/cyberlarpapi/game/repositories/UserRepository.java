package com.example.cyberlarpapi.game.repositories;

import java.util.Optional;
import com.example.cyberlarpapi.game.model.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
