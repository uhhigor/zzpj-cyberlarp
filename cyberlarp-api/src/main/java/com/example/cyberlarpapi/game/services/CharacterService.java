package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.data.Game;
import com.example.cyberlarpapi.game.data.character.Character;
import com.example.cyberlarpapi.game.data.character.CharacterDTO;
import com.example.cyberlarpapi.game.data.character.characterClass.CharacterClass;
import com.example.cyberlarpapi.game.data.character.faction.Faction;
import com.example.cyberlarpapi.game.data.character.style.Style;
import com.example.cyberlarpapi.game.exceptions.*;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private final GameService gameService;

    private final CharacterClassService characterClassService;
    private final CharacterRepository characterRepository;

    private final FactionService factionService;

    private final StyleService styleService;

    public CharacterService(GameService gameService, CharacterClassService characterClassService, CharacterRepository characterRepository, FactionService factionService, StyleService styleService) {
        this.gameService = gameService;
        this.characterClassService = characterClassService;
        this.characterRepository = characterRepository;
        this.factionService = factionService;
        this.styleService = styleService;
    }


    public Character createCharacter(CharacterDTO characterDTO) throws CharacterServiceException {
        try {
            Game game = gameService.getById(characterDTO.getGameId());
            CharacterClass characterClass = characterClassService.getById(characterDTO.getCharacterClassId());
            Faction faction = factionService.getById(characterDTO.getFactionId());
            Style style = styleService.getById(characterDTO.getStyleId());

        Character character = Character.builder()
                .game(game)
                .name(characterDTO.getName())
                .description(characterDTO.getDescription())
                .characterClass(characterClass)
                .faction(faction)
                .style(style)
                .balance(characterDTO.getBalance())
                .strength(characterDTO.getStrength())
                .agility(characterDTO.getAgility())
                .presence(characterDTO.getPresence())
                .toughness(characterDTO.getToughness())
                .knowledge(characterDTO.getKnowledge())
                .max_hp(characterDTO.getMax_hp())
                .armor(characterDTO.getArmor())
                .build();

        return characterRepository.save(character);

        } catch (GameServiceException | FactionServiceException | StyleServiceException | CharacterException e) {
            throw new CharacterServiceException("Error while creating character", e);
        }
    }

}
