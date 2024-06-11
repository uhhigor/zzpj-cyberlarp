package com.example.cyberlarpapi.game.repositories;

import java.util.Optional;
import com.example.cyberlarpapi.game.model.user._User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<_User, Integer> {
    Optional<_User> findByEmail(String email);
}
