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
import java.util.List;
import java.util.Objects;

@Tag(name = "Character Actions", description = "Actions that can be performed by a character")
@RestController
@RequestMapping("/game/{gameId}/action")
public class CharacterActionsController {
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

    public record RollAttributeRequest(Integer characterId, String attribute) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RollAttributeResponse(String message, Integer result) {
    }

    @Operation(summary = "Attribute check roll", description = "Roll an attribute check for a character")
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

        return ResponseEntity.ok(new RollAttributeResponse(null, character.rollAttributeCheck(attributeEnum)));
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
        } catch (BankingServiceException | GameServiceException | UserServiceException e) {
            return ResponseEntity.badRequest().body(new BankingResponse(e.getMessage()));
        } catch (GameNotFoundException | CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Get money transfers of character", description = "Get money transfers of character by providing bank account number")
    @GetMapping("/balance/transfers/{bankAccount}")
    public ResponseEntity<?> getTransactionsOfGame(@PathVariable Integer gameId, @PathVariable String bankAccount) {
        try {
            Game game = gameService.getById(gameId);
            _User user = userService.getCurrentUser();
            Character sender = game.getUserCharacter(user);
            List<Transaction> transactions = moneyService.getTransactions(game, sender, bankAccount);
            return ResponseEntity.ok(transactions);
        } catch (GameNotFoundException | CharacterNotFoundException | UserServiceException e) {
            return ResponseEntity.notFound().build();
        } catch (MoneyServiceException e) {
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
