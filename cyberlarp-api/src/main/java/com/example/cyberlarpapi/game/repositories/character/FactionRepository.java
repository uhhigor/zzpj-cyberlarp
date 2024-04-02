package com.example.cyberlarpapi.game.repositories.character;

import com.example.cyberlarpapi.game.data.character.faction.Faction;
import org.springframework.data.repository.CrudRepository;

public interface FactionRepository extends CrudRepository<Faction, Integer>{
}
