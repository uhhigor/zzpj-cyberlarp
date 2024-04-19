package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterDTO;
import com.example.cyberlarpapi.game.model.character.characterClass.CharacterClass;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.character.style.Style;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionServiceException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.StyleException.StyleServiceException;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private final GameService gameService;

    private final CharacterClassService characterClassService;
    private final CharacterRepository characterRepository;

    private final FactionService factionService;

    private final StyleService styleService;

    private final PlayerService playerService;

    public CharacterService(GameService gameService, CharacterClassService characterClassService, CharacterRepository characterRepository, FactionService factionService, StyleService styleService, PlayerService playerService) {
        this.gameService = gameService;
        this.characterClassService = characterClassService;
        this.characterRepository = characterRepository;
        this.factionService = factionService;
        this.styleService = styleService;
        this.playerService = playerService;
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

    public Character getById(int id) throws CharacterServiceException {
        return characterRepository.findById(id).orElseThrow(() -> new CharacterServiceException("Character not found"));
    }

    public Character update(int id, CharacterDTO characterDTO) throws CharacterServiceException {
        try {
            Character character = getById(id);
            CharacterClass characterClass = characterClassService.getById(characterDTO.getCharacterClassId());
            Faction faction = factionService.getById(characterDTO.getFactionId());
            Style style = styleService.getById(characterDTO.getStyleId());

            character.setName(characterDTO.getName());
            character.setDescription(characterDTO.getDescription());
            character.setCharacterClass(characterClass);
            character.setFaction(faction);
            character.setStyle(style);
            character.setBalance(characterDTO.getBalance());
            character.setStrength(characterDTO.getStrength());
            character.setAgility(characterDTO.getAgility());
            character.setPresence(characterDTO.getPresence());
            character.setToughness(characterDTO.getToughness());
            character.setKnowledge(characterDTO.getKnowledge());
            character.setMax_hp(characterDTO.getMax_hp());
            character.setArmor(characterDTO.getArmor());

            return characterRepository.save(character);
        } catch (FactionServiceException | StyleServiceException | CharacterException e) {
            throw new CharacterServiceException("Error while updating character", e);
        }
    }

    public void deleteById(int id) {
        characterRepository.deleteById(id);
    }

    public Character setPlayer(Character character, int playerId) throws CharacterServiceException {
        try {

            Player player = playerService.getById(playerId);
            character.setPlayer(player); // Set the character to the player
            player.setCharacter(character); // Set the player to the character
            playerService.update(player); // Update the player
            return characterRepository.save(character); // Update the character
        } catch (Exception e) {
            throw new CharacterServiceException("Error while setting player", e);
        }
    }

}
