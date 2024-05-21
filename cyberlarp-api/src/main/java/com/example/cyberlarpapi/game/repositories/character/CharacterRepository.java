package com.example.cyberlarpapi.game.repositories.character;

import com.example.cyberlarpapi.game.model.character.Character;
import org.springframework.data.repository.CrudRepository;

public interface CharacterRepository extends CrudRepository<Character, Integer>{
    Character findByAccountNumber(String accountNumber);
}
