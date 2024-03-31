package com.example.cyberlarpapi.repositories;

import com.example.cyberlarpapi.data.characterClass.CharacterClass;
import org.springframework.data.repository.CrudRepository;

public interface ClassRepository extends CrudRepository<CharacterClass, Integer>{
}
