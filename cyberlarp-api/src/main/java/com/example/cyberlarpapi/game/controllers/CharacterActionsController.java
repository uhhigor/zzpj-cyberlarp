package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.MoneyServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Attribute;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.MoneyService;
import com.example.cyberlarpapi.game.services.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = "Character Actions", description = "Actions that can be performed by a character")
@RestController
@RequestMapping("/game/{gameId}/action")
public class CharacterActionsController {

    public static Map<Integer, LocalTime> lastAttackTime = new HashMap<>();
    public static Map<Integer, LocalTime> lastHealTime = new HashMap<>();
    public static Map<Integer, LocalTime> lastRollTime = new HashMap<>();

    public static final int ROLL_COOLDOWN = 5; // seconds
    public static final int ATTACK_COOLDOWN = 15; // seconds
    public static final int HEAL_COOLDOWN = 60; // seconds


    private final CharacterService characterService;
    private final GameService gameService;

    private final UserService userService;

    private final MoneyService moneyService;

    public CharacterActionsController(CharacterService characterService, GameService gameService, UserService userService, MoneyService moneyService) {
        this.characterService = characterService;
        this.gameService = gameService;
        this.userService = userService;
        this.moneyService = moneyService;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RollAttributeResponse(String message, Integer result) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AttackResponse(String message, Integer attackerId, Integer defenderId, String result, Integer damage) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record HealResponse(String message, Integer characterId, Integer amount) {
    }


    @Operation(summary = "Attribute check roll [CHARACTER]", description = "Roll an attribute check for a character")
    @PostMapping("/roll/{attribute}")
    public ResponseEntity<RollAttributeResponse> roll(@PathVariable Integer gameId, @PathVariable String attribute) {

        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (GameNotFoundException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse(e.getMessage(), null));
        }
        _User user;
        try {
            user = userService.getCurrentUser();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse(e.getMessage(), null));
        }

        Character character;
        try {
            character = game.getUserCharacter(user);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse(e.getMessage(), null));
        }

        Attribute attributeEnum;
        try {
            attributeEnum = Attribute.valueOf(attribute.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("Invalid attribute value: " + attribute, null));
        }
        System.out.println(lastRollTime);
        if(lastRollTime.containsKey(character.getId())) {
            int timeLeft = (int) LocalTime.now().until(lastRollTime.get(character.getId()).plusSeconds(ROLL_COOLDOWN), ChronoUnit.SECONDS);
            if (timeLeft > 0) {
                return ResponseEntity.badRequest().body(new RollAttributeResponse("You need to wait " + timeLeft + " seconds before rolling again", null));
            } else {
                lastRollTime.remove(character.getId());
            }
        }
        lastRollTime.put(character.getId(), LocalTime.now());
        return ResponseEntity.ok(new RollAttributeResponse(null, character.rollAttributeCheck(attributeEnum)));
    }

    @Operation(summary = "Attack another character [CHARACTER]", description = "Attack another character by providing defender character id")
    @PostMapping("/attack/{defenderId}")
    public ResponseEntity<AttackResponse> attack(@PathVariable Integer gameId, @PathVariable Integer defenderId) {
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (GameNotFoundException e) {
            return ResponseEntity.badRequest().body(new AttackResponse(e.getMessage(), null, null, null, null));
        }
        _User user;
        try {
            user = userService.getCurrentUser();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(new AttackResponse(e.getMessage(), null, null, null, null));
        }

        Character attacker;
        try {
            attacker = game.getUserCharacter(user);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new AttackResponse(e.getMessage(), null, null, null, null));
        }

        Character defender;
        try {
            defender = game.getCharacterById(defenderId);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new AttackResponse(e.getMessage(), null, null, null, null));
        }

        if(lastAttackTime.containsKey(attacker.getId())) {
            int timeLeft = (int) LocalTime.now().until(lastAttackTime.get(attacker.getId()).plusSeconds(ATTACK_COOLDOWN), ChronoUnit.SECONDS);
            if (timeLeft > 0) {
                return ResponseEntity.badRequest().body(new AttackResponse("You need to wait " + timeLeft + " seconds before attacking again", null, null, null, null));
            } else {
                lastAttackTime.remove(attacker.getId());
            }
        }

        int rollResult = attacker.rollAttributeCheck(Attribute.STRENGTH) - Math.max(defender.rollAttributeCheck(Attribute.TOUGHNESS), defender.rollAttributeCheck(Attribute.AGILITY));

        if(rollResult <= 0) {
            return ResponseEntity.ok(new AttackResponse("", attacker.getId(), defender.getId(), "Miss!", 0));
        }
        int damage = attacker.getAttribute(Attribute.STRENGTH);
        int result = defender.takeDamage(damage);
        characterService.save(defender);

        lastAttackTime.put(attacker.getId(), LocalTime.now());
        return ResponseEntity.ok(new AttackResponse("", attacker.getId(), defender.getId(), "Hit!", result));
    }

    @Operation(summary = "Heal another character [CHARACTER]", description = "Heal another character by providing character id")
    @PostMapping("/heal/{characterId}")
    public ResponseEntity<HealResponse> heal(@PathVariable Integer gameId, @PathVariable Integer characterId) {
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (GameNotFoundException e) {
            return ResponseEntity.badRequest().body(new HealResponse(e.getMessage(), null, null));
        }
        _User user;
        try {
            user = userService.getCurrentUser();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(new HealResponse(e.getMessage(), null, null));
        }

        Character healer;
        try {
            healer = game.getUserCharacter(user);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new HealResponse(e.getMessage(), null, null));
        }

        Character character;
        try {
            character = game.getCharacterById(characterId);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new HealResponse(e.getMessage(), null, null));
        }

        if(lastHealTime.containsKey(healer.getId())) {
            int timeLeft = (int) LocalTime.now().until(lastHealTime.get(healer.getId()).plusSeconds(HEAL_COOLDOWN), ChronoUnit.SECONDS);
            if (timeLeft > 0) {
                return ResponseEntity.badRequest().body(new HealResponse("You need to wait " + timeLeft + " seconds before healing again", null, null));
            } else {
                lastHealTime.remove(healer.getId());
            }
        }

        int roll = healer.rollAttributeCheck(Attribute.KNOWLEDGE);
        int amount;
        if (roll < 10) {
            amount = 1;
        } else if (roll < 15) {
            amount = 2;
        } else {
            amount = 3;
        }
        character.heal(amount);
        characterService.save(character);

        lastHealTime.put(healer.getId(), LocalTime.now());
        return ResponseEntity.ok(new HealResponse("", character.getId(), amount));
    }


    // ====================== Banking ========================== //

    @Operation(summary = "Transfer money between characters", description = "Transfer money between characters by providing sender and receiver account numbers and amount")
    @PostMapping("/balance/transfer")
    public ResponseEntity<BankingResponse> create(@RequestBody BankingRequest request, @PathVariable Integer gameId) {
        try {
            Game game = gameService.getById(gameId);
            _User user = userService.getCurrentUser();
            Character sender = game.getUserCharacter(user);
            if(!Objects.equals(sender.getAccountNumber(), request.senderBankAccount)) {
                return ResponseEntity.badRequest().body(new BankingResponse("Not authorized to transfer money from this account"));
            }

            Transaction transaction = moneyService.transferMoney(request.senderBankAccount, request.receiverBankAccount, request.amount, game);
            return ResponseEntity.ok(new BankingResponse(transaction));
        } catch (BankingServiceException | GameServiceException | UserServiceException | CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new BankingResponse(e.getMessage()));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Get money transfers of character [CHARACTER]", description = "Get money transfers of character by providing bank account number")
    @GetMapping("/balance/transfers/{bankAccount}")
    public ResponseEntity<?> getTransactionsOfGame(@PathVariable Integer gameId, @PathVariable String bankAccount) {
        try {
            Game game = gameService.getById(gameId);
            _User user = userService.getCurrentUser();
            Character sender = game.getUserCharacter(user);
            List<Transaction> transactions = moneyService.getTransactions(game, sender, bankAccount);
            return ResponseEntity.ok(transactions);
        } catch (GameNotFoundException | UserServiceException e) {
            return ResponseEntity.notFound().build();
        } catch (MoneyServiceException | CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(hidden = true)
    public static class BankingRequest {
        private String senderBankAccount;
        private String receiverBankAccount;
        private int amount;
    }

    @Getter
    @NoArgsConstructor
    @Schema(hidden = true)
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
                this.senderAccountNumber = transaction.getSenderAccount();
                this.receiverAccountNumber = transaction.getReceiverAccount();
                this.amount = transaction.getAmount();
                this.timestamp = transaction.getTimestamp();
            }
        }
    }
}
