package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.model.character.Attribute;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
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
import java.util.Objects;

@Tag(name = "Character Actions", description = "Actions that can be performed by a character")
@RestController
@RequestMapping("/game/{gameId}/action")
public class CharacterActionsController {
    private final CharacterService characterService;
    private final GameService gameService;

    private final UserService userService;

    public CharacterActionsController(CharacterService characterService, GameService gameService, UserService userService) {
        this.characterService = characterService;
        this.gameService = gameService;
        this.userService = userService;
    }

    public record RollAttributeRequest(Integer characterId, String attribute) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RollAttributeResponse(String message, Integer result) {
    }

    @Operation(summary = "Attribute check roll", description = "Roll an attribute check for a character")
    @PostMapping("/roll")
    public ResponseEntity<RollAttributeResponse> roll(@RequestBody RollAttributeRequest request, @PathVariable Integer gameId) {

        Game game;
        Character character;
        try {
            game = gameService.getById(gameId);
            character = characterService.getById(request.characterId);
        } catch (GameNotFoundException | CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse(e.getMessage(), null));
        }
        _User user;
        try {
            user = userService.getCurrentUser();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse(e.getMessage(), null));
        }

        if(!Objects.equals(user.getId(), character.getUser().getId())) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("You are not the owner of this character", null));
        }

        Attribute attribute;
        try {
            attribute = Attribute.valueOf(request.attribute.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("Invalid attribute value", null));
        }

        return ResponseEntity.ok(new RollAttributeResponse(null, character.rollAttributeCheck(attribute)));
    }

    // ====================== Banking ========================== //

    @Operation(summary = "Transfer money between characters", description = "Transfer money between characters by providing sender and receiver account numbers and amount")
    @PostMapping("/moneytransfer")
    public ResponseEntity<BankingResponse> create(@RequestBody BankingRequest request) {
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
    @Schema(hidden = true)
    public static class BankingRequest {
        private String senderBankAccount;
        private String receiverBankAccount;
        private int amount;
        private Integer gameId;
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
                this.senderAccountNumber = transaction.getSender().getAccountNumber();
                this.receiverAccountNumber = transaction.getReceiver().getAccountNumber();
                this.amount = transaction.getAmount();
                this.timestamp = transaction.getTimestamp();
            }
        }
    }
}
