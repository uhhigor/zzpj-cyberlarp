package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.model.character.Character;

import java.util.List;

@Controller
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

    @GetMapping
    public ResponseEntity<List<GameResponse>> getAllGames() {
        return ResponseEntity.ok(gameService.getAll().stream().map(GameResponse::new).toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new GameResponse(gameService.getById(id)));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<GameResponse> deleteGame(@PathVariable Integer id) {
        try {
            gameService.deleteById(id);
            return ResponseEntity.ok(new GameResponse("Game deleted successfully"));
        } catch (GameServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{id}/player/{playerId}")
    public ResponseEntity<GameResponse> addPlayerToGame(@PathVariable Integer id, @PathVariable Integer playerId) {
        try {
            Game game = gameService.getById(id);
            //gameService.addPlayerToGame(id, player);
            return ResponseEntity.ok(new GameResponse("Player added to game successfully", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/player/{playerId}")
    public ResponseEntity<GameResponse> removePlayerFromGame(@PathVariable Integer id, @PathVariable Integer playerId) {
        try {
            Game game = gameService.getById(id);
            //gameService.kickPlayerFromGame(id, player);
            return ResponseEntity.ok(new GameResponse("Player removed from game successfully", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

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
            private List<Integer> playerIds;
            private List<Integer> availableCharacterIds;


            public GameData(Game game) {
                this.id = game.getId();
                this.name = game.getName();
                this.description = game.getDescription();
                this.gameMasterId = game.getGameMaster().getId();
                this.playerIds = game.getUsers().stream().map(_User::getId).toList();
                this.availableCharacterIds = game.getAvailableCharacters().stream().map(Character::getId).toList();
            }
        }
    }

}
