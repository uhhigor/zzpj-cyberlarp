package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerNotFoundException;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.character.Attribute;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.character.Style;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.FactionService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.PlayerService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;

    private final FactionService factionService;

    private final GameService gameService;

    private final PlayerService playerService;

    public CharacterController(CharacterService characterService, FactionService factionService, GameService gameService, PlayerService playerService) {
        this.characterService = characterService;
        this.factionService = factionService;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new CharacterResponse(characterService.getById(id)));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CharacterResponse> deleteCharacter(@PathVariable Integer id) {
        try {
            characterService.deleteById(id);
            return ResponseEntity.ok(new CharacterResponse("Character " + id + " deleted successfully"));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Character createAndSaveCharacter(CharacterRequest request) throws CharacterException {
        Faction faction = null;
        if(request.getFactionId() != null) {
            try {
                faction = factionService.getById(request.getFactionId());
            } catch (FactionNotFoundException e) {
                throw new CharacterException("Invalid faction");
            }
        }
        Style style;
        try {
            style = Style.valueOf(request.getStyle());
        } catch (IllegalArgumentException e) {
            throw new CharacterException("Invalid style");
        }
        CharacterClass characterClass;
        try {
            characterClass = CharacterClass.valueOf(request.getCharacterClass());
        } catch (IllegalArgumentException e) {
            throw new CharacterException("Invalid character class");
        }
        Character character = Character.builder()
                .name(request.getName())
                .description(request.getDescription())
                .characterClass(characterClass)
                .faction(faction)
                .style(style)
                .strength(request.getStrength())
                .agility(request.getAgility())
                .presence(request.getPresence())
                .toughness(request.getToughness())
                .knowledge(request.getKnowledge())
                .maxHp(request.getMaxHp())
                .balance(request.getBalance())
                .build();
        return characterService.save(character);
    }

    @PostMapping("/game/{gameId}")
    public ResponseEntity<CharacterResponse> addCharacterToGame(@RequestBody CharacterRequest request, @PathVariable Integer gameId) {
        try {
            Game game = gameService.getById(gameId);
            Character character = createAndSaveCharacter(request);
            game.addAvailableCharacter(character);
            return ResponseEntity.ok(new CharacterResponse("Character " + character.getId() + " added to game " + game.getId(), characterService.save(character)));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CharacterException e) {
            return ResponseEntity.badRequest().body(new CharacterResponse(e.getMessage()));
        }
    }

    @PostMapping("/player/{playerId}")
    public ResponseEntity<CharacterResponse> addCharacterToPlayer(@RequestBody CharacterRequest request, @PathVariable Integer playerId) {
        try {
            Player player = playerService.getById(playerId);
            Character character = createAndSaveCharacter(request);
            player.setCharacter(character);
            playerService.save(player);
            return ResponseEntity.ok(new CharacterResponse("Character " + character.getId() + " added to player " + player.getId(), characterService.save(character)));
        } catch (PlayerNotFoundException | CharacterException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<CharacterResponse> updateCharacter(@PathVariable Integer id, @RequestBody CharacterRequest request) {
        try {
            Character character = characterService.getById(id);
            Faction faction = factionService.getById(request.getFactionId());
            character.setName(request.getName());
            character.setDescription(request.getDescription());
            character.setCharacterClass(CharacterClass.valueOf(request.getCharacterClass()));
            character.setFaction(faction);
            character.setStyle(Style.valueOf(request.getStyle()));
            character.setAttribute(Attribute.STRENGTH, request.getStrength());
            character.setAttribute(Attribute.AGILITY, request.getAgility());
            character.setAttribute(Attribute.PRESENCE, request.getPresence());
            character.setAttribute(Attribute.TOUGHNESS, request.getToughness());
            character.setAttribute(Attribute.KNOWLEDGE, request.getKnowledge());
            character.setMaxHp(request.getMaxHp());
            return ResponseEntity.ok(new CharacterResponse("Character " + id + " updated successfully", characterService.save(character)));
        } catch (CharacterNotFoundException | FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CharacterRequest {
        private String name;
        private String description;
        private String characterClass;
        private Integer factionId;
        private String style;
        private Integer strength;
        private Integer agility;
        private Integer presence;
        private Integer toughness;
        private Integer knowledge;
        private Integer maxHp;
        private Integer currentHp;
        private Integer balance;
    }

    @Getter
    @NoArgsConstructor
    public static class CharacterResponse {

        private String message;
        private CharacterData character;

        public CharacterResponse(String message, Character character) {
            this.message = message;
            this.character = new CharacterData(character);
        }

        public CharacterResponse(Character character) {
            this.character = new CharacterData(character);
        }

        public CharacterResponse(String message) {
            this.message = message;
        }

        @Setter
        @Getter
        @NoArgsConstructor
        public static class CharacterData {
            private Integer id;
            private String name;
            private String description;
            private String characterClass;
            private Integer factionId;
            private String style;
            private Integer strength;
            private Integer agility;
            private Integer presence;
            private Integer toughness;
            private Integer knowledge;
            private Integer maxHp;
            private Integer currentHp;
            private Integer balance;
            private String accountNumber;
            private Integer armor;

            public CharacterData(Character character) {
                this.id = character.getId();
                this.name = character.getName();
                this.description = character.getDescription();
                this.characterClass = character.getCharacterClass().name();
                this.factionId = character.getFaction() == null ? null : character.getFaction().getId();
                this.style = character.getStyle().name();
                this.strength = character.getAttribute(Attribute.STRENGTH);
                this.agility = character.getAttribute(Attribute.AGILITY);
                this.presence = character.getAttribute(Attribute.PRESENCE);
                this.toughness = character.getAttribute(Attribute.TOUGHNESS);
                this.knowledge = character.getAttribute(Attribute.KNOWLEDGE);
                this.maxHp = character.getMaxHp();
                this.currentHp = character.getCurrentHp();
                this.armor = character.getArmor();
                this.balance = character.getBalance();
                this.accountNumber = character.getAccount_number();
            }
        }
    }
}
