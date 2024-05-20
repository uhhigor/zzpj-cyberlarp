package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionNotFoundException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.character.Style;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.FactionService;
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

    public CharacterController(CharacterService characterService, FactionService factionService) {
        this.characterService = characterService;
        this.factionService = factionService;
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

    @PostMapping
    public ResponseEntity<CharacterResponse> createCharacter(@RequestBody CharacterRequest request) {
        try {
            Faction faction = factionService.getById(request.getFactionId());
            Character character = Character.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .characterClass(CharacterClass.valueOf(request.getCharacterClass()))
                    .faction(faction)
                    .style(Style.valueOf(request.getStyle()))
                    .strength(request.getStrength())
                    .agility(request.getAgility())
                    .presence(request.getPresence())
                    .toughness(request.getToughness())
                    .knowledge(request.getKnowledge())
                    .maxHp(request.getMaxHp())
                    .balance(request.getBalance())
                    .build();
            return ResponseEntity.ok(new CharacterResponse(characterService.save(character)));
        } catch (FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CharacterException e) {
            return ResponseEntity.badRequest().body(new CharacterResponse(e.getMessage()));
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
            character.setStrength(request.getStrength());
            character.setAgility(request.getAgility());
            character.setPresence(request.getPresence());
            character.setToughness(request.getToughness());
            character.setKnowledge(request.getKnowledge());
            character.setMaxHp(request.getMaxHp());
            return ResponseEntity.ok(new CharacterResponse(characterService.save(character)));
        } catch (CharacterNotFoundException | FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CharacterRequest {
        private Integer gameId;
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

            public CharacterData(Character character) {
                this.id = character.getId();
                this.name = character.getName();
                this.description = character.getDescription();
                this.characterClass = character.getCharacterClass().name();
                this.factionId = character.getFaction().getId();
                this.style = character.getStyle().name();
                this.strength = character.getStrength();
                this.agility = character.getAgility();
                this.presence = character.getPresence();
                this.toughness = character.getToughness();
                this.knowledge = character.getKnowledge();
                this.maxHp = character.getMaxHp();
                this.currentHp = character.getCurrentHp();
                this.balance = character.getBalance();
                this.accountNumber = character.getAccount_number();
            }
        }
    }
}
