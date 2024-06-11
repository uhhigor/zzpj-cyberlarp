package com.example.cyberlarpapi.game.repositories.character;

import java.util.List;
import java.util.Optional;
import com.example.cyberlarpapi.game.model.character.Character;
import org.springframework.data.repository.CrudRepository;

public interface CharacterRepository extends CrudRepository<Character, Integer>{
    Character findByAccountNumber(String accountNumber);

    Optional<Character> findByUserId(int userId);

    List<Character> findAllByUserId(int userId);
}
