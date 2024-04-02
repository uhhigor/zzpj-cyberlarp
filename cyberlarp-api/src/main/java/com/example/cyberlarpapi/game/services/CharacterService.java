package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.data.character.CharacterDTO;
import com.example.cyberlarpapi.game.data.character.Character;
import com.example.cyberlarpapi.game.data.character.characterClass.CharacterClass;
import com.example.cyberlarpapi.game.data.character.faction.Faction;
import com.example.cyberlarpapi.game.data.character.style.Style;
import com.example.cyberlarpapi.game.exceptions.CharacterException;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import com.example.cyberlarpapi.game.repositories.character.ClassRepository;
import com.example.cyberlarpapi.game.repositories.character.FactionRepository;
import com.example.cyberlarpapi.game.repositories.character.StyleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final ClassRepository classRepository;
    private final FactionRepository factionRepository;
    private final StyleRepository styleRepository;

    @Autowired
    public CharacterService(CharacterRepository characterRepository, ClassRepository classRepository, FactionRepository factionRepository, StyleRepository styleRepository) {
        this.characterRepository = characterRepository;
        this.classRepository = classRepository;
        this.factionRepository = factionRepository;
        this.styleRepository = styleRepository;
    }

    public void createCharacter(CharacterDTO characterDTO) throws CharacterException {
        CharacterClass characterClass = classRepository.findById(characterDTO.getCharacterClassId()).orElseThrow(() -> new CharacterException("Class not found"));
        Faction faction = factionRepository.findById(characterDTO.getFactionId()).orElseThrow(() -> new CharacterException("Faction not found"));
        Style style = styleRepository.findById(characterDTO.getStyleId()).orElseThrow(() -> new CharacterException("Style not found"));

        Character character = new Character(characterDTO.getName(), characterDTO.getDescription(), characterClass, faction, style,
                        characterDTO.getBalance(), characterDTO.getStrength(), characterDTO.getAgility(),
                        characterDTO.getPresence(), characterDTO.getToughness(), characterDTO.getKnowledge(),
                        characterDTO.getMax_hp(), characterDTO.getArmor());

        characterRepository.save(character);
    }

}
