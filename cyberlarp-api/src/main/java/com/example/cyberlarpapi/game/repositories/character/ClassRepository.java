package com.example.cyberlarpapi.game.repositories.character;

import com.example.cyberlarpapi.game.data.character.characterClass.CharacterClass;
import org.springframework.data.repository.CrudRepository;

public interface ClassRepository extends CrudRepository<CharacterClass, Integer>{
}
