
package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.*;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Character Operations", description = "Operations on characters")
@RestController
@RequestMapping("/game/{gameId}/character")
public class CharacterController {

    private final CharacterService characterService;
    private final GameService gameService;

    private final UserService userService;

    public CharacterController(CharacterService characterService, GameService gameService, UserService userService) {
        this.characterService = characterService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @Operation(summary = "Get character by id [GM/Player]", description = "Get character by id by providing character id and game id")
    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable Integer id, @PathVariable Integer gameId) {
        try {
            Game game = gameService.getById(gameId);
            _User sender = userService.getCurrentUser();
            Character character = characterService.getById(id);
            if(game.getGameMaster().getId().equals(sender.getId())
                    || (character.getUser() != null && character.getUser().getId().equals(sender.getId())))
                return ResponseEntity.ok(new CharacterResponse(character));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (GameNotFoundException | UserServiceException e) {
            return ResponseEntity.badRequest().body(new CharacterResponse(e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new CharacterResponse("You are not allowed to view this character"));
    }

    @Operation(summary = "Get current character [PLAYER]", description = "Get current character by providing game id")
    @GetMapping("/")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable Integer gameId) {
        try {
            Game game = gameService.getById(gameId);
            _User sender = userService.getCurrentUser();
            return ResponseEntity.ok(new CharacterResponse(game.getUserCharacter(sender)));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (GameNotFoundException | UserServiceException e) {
            return ResponseEntity.badRequest().body(new CharacterResponse(e.getMessage()));
        }
    }

    @Operation(summary = "Delete character [GM]", description = "Delete character by id by providing character id and game id")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<CharacterResponse> deleteCharacter(@PathVariable Integer characterId, @PathVariable Integer gameId) {
        try {
            Game game = gameService.getById(gameId);
            _User user = userService.getCurrentUser();
            if(!game.getGameMaster().getId().equals(user.getId()))
                return ResponseEntity.badRequest().body(new CharacterResponse("Only game master can delete characters"));

            game.removeCharacter(characterService.getById(characterId));
            gameService.save(game);
            characterService.deleteById(characterId);
            return ResponseEntity.ok(new CharacterResponse("Character " + characterId + " deleted successfully"));
        } catch (CharacterNotFoundException | GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(new CharacterResponse(e.getMessage()));
        }
    }

    private Character createAndSaveCharacter(CharacterRequest request) throws CharacterException {
        Faction faction;
        try {
            faction = Faction.valueOf(request.getFaction());
        } catch (IllegalArgumentException e) {
            throw new CharacterException("Invalid faction");
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
                .armor(request.getArmor())
                .build();
        Character savedCharacter = characterService.save(character);
        return savedCharacter;
    }

    @Operation(summary = "Add new character to game [GM]", description = "Add new character to game by providing character details and game id")
    @PostMapping("/")
    public ResponseEntity<CharacterResponse> addCharacterToGame(@RequestBody CharacterRequest request, @PathVariable Integer gameId) {
        try {
            _User sender = userService.getCurrentUser();
            Game game = gameService.getById(gameId);
            if(!game.getGameMaster().getId().equals(sender.getId()))
                return ResponseEntity.badRequest().body(new CharacterResponse("Only game master can add characters"));

            Character character = createAndSaveCharacter(request);
            game.addCharacter(character);
            gameService.save(game);
            return ResponseEntity.ok(new CharacterResponse("Character " + character.getId() + " added to game " + game.getId(), characterService.save(character)));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CharacterException e) {
            return ResponseEntity.badRequest().body(new CharacterResponse(e.getMessage()));
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Assign character to user [GM]", description = "Assign character to user by providing character id and user id")
    @PostMapping("/{characterId}/assignUser/{userId}")
    public ResponseEntity<CharacterResponse> assignCharacterToUser(@PathVariable Integer gameId, @PathVariable Integer characterId, @PathVariable Integer userId) {
        try {
            _User sender = userService.getCurrentUser();
            Game game = gameService.getById(gameId);
            if(!game.getGameMaster().getId().equals(sender.getId()))
                return ResponseEntity.badRequest().body(new CharacterResponse("Only game master can assign characters"));
            Character character = characterService.getById(characterId);
            if(!game.getCharacters().contains(character))
                return ResponseEntity.badRequest().body(new CharacterResponse("Character " + characterId + " is not in game " + gameId));

            _User user = userService.getUserById(userId);
            character.setUser(user);
            character = characterService.save(character);
            return ResponseEntity.ok(new CharacterResponse("Character " + characterId + " assigned to user " + userId, character));
        } catch (GameNotFoundException | CharacterNotFoundException | UserServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update character [GM]", description = "Update character by providing character details and character id")
    @PostMapping("/{id}")
    public ResponseEntity<CharacterResponse> updateCharacter(@PathVariable Integer id, @RequestBody CharacterRequest request, @PathVariable Integer gameId) {
        try {
            _User sender = userService.getCurrentUser();
            Game game = gameService.getById(gameId);
            if(!game.getGameMaster().getId().equals(sender.getId()))
                return ResponseEntity.badRequest().body(new CharacterResponse("Only game master can update characters"));
            Character character = characterService.getById(id);
            if(request.getName() != null)
                character.setName(request.getName());
            if(request.getDescription() != null)
                character.setDescription(request.getDescription());
            if(request.getCharacterClass() != null)
                character.setCharacterClass(CharacterClass.valueOf(request.getCharacterClass()));
            if(request.getFaction() != null)
                character.setFaction(Faction.valueOf(request.getFaction()));
            if(request.getStyle() != null)
                character.setStyle(Style.valueOf(request.getStyle()));
            if(request.getStrength() != null)
                character.setAttribute(Attribute.STRENGTH, request.getStrength());
            if(request.getAgility() != null)
                character.setAttribute(Attribute.AGILITY, request.getAgility());
            if(request.getPresence() != null)
                character.setAttribute(Attribute.PRESENCE, request.getPresence());
            if(request.getToughness() != null)
                character.setAttribute(Attribute.TOUGHNESS, request.getToughness());
            if(request.getKnowledge() != null)
                character.setAttribute(Attribute.KNOWLEDGE, request.getKnowledge());
            if(request.getMaxHp() != null)
                character.setMaxHp(request.getMaxHp());
            if(request.getArmor() != null)
                character.setArmor(request.getArmor());
            return ResponseEntity.ok(new CharacterResponse("Character " + id + " updated successfully", characterService.save(character)));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException | GameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(hidden = true)
    public static class CharacterRequest {
        private String name;
        private String description;
        private String characterClass;
        private String faction;
        private String style;
        private Integer strength;
        private Integer agility;
        private Integer presence;
        private Integer toughness;
        private Integer knowledge;
        private Integer maxHp;
        private Integer armor;
        private Float balance;
    }

    @Getter
    @NoArgsConstructor
    @Schema(hidden = true)
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
            private Integer userId;
            private Integer id;
            private String name;
            private String description;
            private String characterClass;
            private String faction;
            private String style;
            private Integer strength;
            private Integer agility;
            private Integer presence;
            private Integer toughness;
            private Integer knowledge;
            private Integer maxHp;
            private Integer currentHp;
            private Float balance;
            private String accountNumber;
            private Integer armor;

            public CharacterData(Character character) {
                if(character.getUser() != null)
                    this.userId = character.getUser().getId();
                this.id = character.getId();
                this.name = character.getName();
                this.description = character.getDescription();
                if(character.getCharacterClass() != null)
                    this.characterClass = character.getCharacterClass().name();
                if(character.getFaction() != null)
                    this.faction = character.getFaction().name();
                if(character.getStyle() != null)
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
                this.accountNumber = character.getAccountNumber();
            }
        }
    }
}
