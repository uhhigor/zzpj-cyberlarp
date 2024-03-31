package com.example.cyberlarpapi.services;

import com.example.cyberlarpapi.data.character.CharacterDTO;
import com.example.cyberlarpapi.data.character.Character;
import com.example.cyberlarpapi.data.characterClass.CharacterClass;
import com.example.cyberlarpapi.data.faction.Faction;
import com.example.cyberlarpapi.repositories.CharacterRepository;
import com.example.cyberlarpapi.repositories.ClassRepository;
import com.example.cyberlarpapi.repositories.FactionRepository;
import com.example.cyberlarpapi.repositories.StyleRepository;
import com.example.cyberlarpapi.data.style.Style;
import com.example.cyberlarpapi.exceptions.CharacterException;
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
