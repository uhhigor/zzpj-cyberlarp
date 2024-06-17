package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.model.character.Character;

import java.util.List;

@Tag(name = "Game Operations", description = "Operations related to games in the system")
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    private final UserService userService;

    private final CharacterService characterService;

    public GameController(GameService gameService, UserService userService, CharacterService characterService) {
        this.gameService = gameService;
        this.userService = userService;
        this.characterService = characterService;
    }

    @Operation(summary = "Create a new game", description = "Create a new game in the system, providing name, description and game master user id")
    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest request) {
        try {
            _User gameMaster = userService.getUserById(request.getGameMasterUserId());
            Game game = Game.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .gameMaster(gameMaster)
                    .build();
            game = gameService.save(game);
            return ResponseEntity.ok(new GameResponse("Game created successfully", game));
        } catch (UserServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all games", description = "Get all games in the system")
    @GetMapping
    public ResponseEntity<List<GameResponse>> getAllGames() {
        return ResponseEntity.ok(gameService.getAll().stream().map(GameResponse::new).toList());
    }


    @Operation(summary = "Get game by id", description = "Get game by id")
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new GameResponse(gameService.getById(id)));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update game text data by id", description = "Update game text data by id, providing name and description")
    @PutMapping("/{id}/textData")
    public ResponseEntity<GameResponse> updateGameTextDataById(@PathVariable Integer id, @RequestBody GameRequest request) {
        try {
            Game game = gameService.getById(id);
            game.setName(request.getName());
            game.setDescription(request.getDescription());
            game = gameService.save(game);
            return ResponseEntity.ok(new GameResponse("Game updated successfully", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete game by id", description = "Delete game by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<GameResponse> deleteGame(@PathVariable Integer id) {
        try {
            gameService.deleteById(id);
            return ResponseEntity.ok(new GameResponse("Game deleted successfully"));
        } catch (GameServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Add character to game", description = "Add character to game by providing game id and character id")
    @PostMapping("/{id}/character/{characterId}")
    public ResponseEntity<GameResponse> addCharacterToGame(@PathVariable Integer id, @PathVariable Integer characterId) {
        try {
            Game game = gameService.getById(id);
            Character character = characterService.getById(characterId);
            gameService.addCharacterToGame(id, character);
            return ResponseEntity.ok(new GameResponse("Player added to game successfully", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CharacterNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Remove character from game", description = "Remove character from game by providing game id and character id")
    @DeleteMapping("/{id}/character/{characterId}")
    public ResponseEntity<GameResponse> removeCharacterFromGame(@PathVariable Integer id, @PathVariable Integer characterId) {
        try {
            Game game = gameService.getById(id);
            Character character = characterService.getById(characterId);
            gameService.kickCharacterFromGame(id, character);
            return ResponseEntity.ok(new GameResponse("Player removed from game successfully", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CharacterNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Make user owner of game", description = "Make user owner of game by providing game id and user id")
    @PutMapping("/{id}/gameMaster/{userId}")
    public ResponseEntity<GameResponse> makeUserOwnerOfGame(@PathVariable Integer id, @PathVariable Integer userId) {
        try {
            Game game = gameService.getById(id);
            _User user = userService.getUserById(userId);
            gameService.makeUserOwnerOfGame(id, user);
            return ResponseEntity.ok(new GameResponse("User is now the owner of the game", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
        }
    }

    // ====================== Banking ========================== //
    @Operation(summary = "Get transactions of game", description = "Get transactions of game by providing sender bank account and game id")
    @PostMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsOfGame(@RequestBody CharacterController.BankingRequest request) {
        try {
            List<Transaction> transactions = gameService.getTransactions(request.getSenderBankAccount(), request.getGameId());
            return ResponseEntity.ok(transactions);
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (GameServiceException | BankingServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GameRequest {
        private String name;
        private String description;
        private Integer gameMasterUserId;
    }

    @Getter
    @NoArgsConstructor
    public static class GameResponse {
        private String message;
        private GameData game;

        public GameResponse(String message, Game game) {
            this.message = message;
            this.game = new GameData(game);
        }

        public GameResponse(Game game) {
            this.game = new GameData(game);
        }

        public GameResponse(String message) {
            this.message = message;
        }

        @Getter
        public static class GameData {
            private Integer id;
            private String name;
            private String description;
            private Integer gameMasterId;
            private List<Integer> charactersId;
            private List<Integer> availableCharacterIds;


            public GameData(Game game) {
                this.id = game.getId();
                this.name = game.getName();
                this.description = game.getDescription();
                this.gameMasterId = game.getGameMaster().getId();
                this.charactersId = game.getCharacters().stream().map(Character::getId).toList();
                this.availableCharacterIds = game.getAvailableCharacters().stream().map(Character::getId).toList();
            }
        }
    }

}
