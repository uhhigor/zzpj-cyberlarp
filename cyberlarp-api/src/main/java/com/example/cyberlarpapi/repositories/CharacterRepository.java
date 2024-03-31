package com.example.cyberlarpapi.repositories;

import com.example.cyberlarpapi.data.character.Character;
import org.springframework.data.repository.CrudRepository;

public interface CharacterRepository extends CrudRepository<Character, Integer>{
}
