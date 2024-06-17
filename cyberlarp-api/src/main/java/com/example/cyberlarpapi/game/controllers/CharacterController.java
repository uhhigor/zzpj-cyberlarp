
package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Attribute;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.character.Style;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Character Operations", description = "Operations on characters")
@RestController
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;

    private final FactionService factionService;

    private final GameService gameService;

    private final UserService userService;

    public CharacterController(CharacterService characterService, FactionService factionService, GameService gameService, UserService userService) {
        this.characterService = characterService;
        this.factionService = factionService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @Operation(summary = "Get character by id")
    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new CharacterResponse(characterService.getById(id)));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete character by id and user id")
    @DeleteMapping("/{characterId}/{userId}")
    public ResponseEntity<CharacterResponse> deleteCharacter(@PathVariable Integer characterId, @PathVariable Integer userId) {
        try {
            _User user = userService.getUserById(userId);
            user.getCharacters().removeIf(character -> character.getId().equals(characterId));
            characterService.deleteById(characterId);
            return ResponseEntity.ok(new CharacterResponse("Character " + characterId + " deleted successfully"));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
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
        _User user;
        try {
            user = userService.getUserById(request.getUserId());
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
        }
        Game game;
        try {
            game = gameService.getById(request.getGameId());
        } catch (GameNotFoundException e) {
            throw new CharacterException("Invalid game");
        }

        Character character = Character.builder()
                .user(user)
                .game(game)
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
        user.addCharacter(character);
        return characterService.save(character);
    }

    @Operation(summary = "Add new character to game", description = "Add new character to game by providing character details and game id")
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

    @Operation(summary = "Update character", description = "Update character by providing character details and character id")
    @PostMapping("/{id}")
    public ResponseEntity<CharacterResponse> updateCharacter(@PathVariable Integer id, @RequestBody CharacterRequest request) {
        try {
            Character character = characterService.getById(id);
            Faction faction = factionService.getById(request.getFactionId());
            _User user = userService.getUserById(request.getUserId());
            Game game = gameService.getById(request.getGameId());
            character.setUser(user);
            character.setGame(game);
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
        } catch (UserServiceException | GameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CharacterRequest {
        private Integer userId;
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
        private Float balance;
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
            private Integer userId;
            private Integer gameId;
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
            private Float balance;
            private String accountNumber;
            private Integer armor;

            public CharacterData(Character character) {
                this.userId = character.getUser().getId();
                this.gameId = character.getGame().getId();
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
                this.accountNumber = character.getAccountNumber();
            }
        }
    }

    // ====================== Banking ========================== //

    @Operation(summary = "Transfer money between characters", description = "Transfer money between characters by providing sender and receiver account numbers and amount")
    @PostMapping("/transfer")
    public ResponseEntity<CharacterController.BankingResponse> create(@RequestBody CharacterController.BankingRequest request) {
        try {
            Transaction newTransaction = characterService.transferMoney(request.getSenderBankAccount(),
                                                                        request.getReceiverBankAccount(),
                                                                        request.getAmount(),
                                                                        request.getGameId());

            gameService.addTransaction(newTransaction, request.getGameId());
            return ResponseEntity.ok(new BankingResponse(newTransaction));
        } catch (BankingServiceException | GameServiceException e) {
            return ResponseEntity.badRequest().body(new BankingResponse(e.getMessage()));
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BankingRequest {
        private String senderBankAccount;
        private String receiverBankAccount;
        private int amount;
        private Integer gameId;
    }

    @Getter
    @NoArgsConstructor
    public static class BankingResponse {
        private String message;
        private TransactionData transaction;

        public BankingResponse(String message, Transaction transaction) {
            this.message = message;
            this.transaction = new TransactionData(transaction);
        }

        public BankingResponse(Transaction transaction) {
            this.transaction = new TransactionData(transaction);
        }

        public BankingResponse(String message) {
            this.message = message;
        }

        @Getter
        public static class TransactionData {
            private Integer id;
            private String senderAccountNumber;
            private String receiverAccountNumber;
            private int amount;
            private LocalDateTime timestamp;

            public TransactionData(Transaction transaction) {
                this.id = transaction.getId();
                this.senderAccountNumber = transaction.getSender().getAccountNumber();
                this.receiverAccountNumber = transaction.getReceiver().getAccountNumber();
                this.amount = transaction.getAmount();
                this.timestamp = transaction.getTimestamp();
            }
        }
    }

}
