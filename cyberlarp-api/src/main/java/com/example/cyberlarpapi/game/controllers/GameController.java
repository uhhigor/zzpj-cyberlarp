package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.DefaultGameData;
import com.example.cyberlarpapi.game.exceptions.BankingException.BankingServiceException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.MessageNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.model.chat.SCOPE;
import com.example.cyberlarpapi.game.model.chat.message.Message;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.Transaction;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.MessageService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.model.character.Character;

import java.util.List;
import java.util.Objects;

@Tag(name = "Game Operations", description = "Operations related to games in the system")
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    private final UserService userService;

    private final CharacterService characterService;

    private final MessageService messageService;

    public GameController(GameService gameService, UserService userService, CharacterService characterService, MessageService messageService) {
        this.gameService = gameService;
        this.userService = userService;
        this.characterService = characterService;
        this.messageService = messageService;
    }

    @Operation(summary = "Create a new game", description = "Create a new game in the system, providing name, description and game master user id")
    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest request) {
        try {
            _User gameMaster = userService.getCurrentUser();
            Game game = Game.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .gameMaster(gameMaster)
                    .build();
            game = gameService.save(game);
            List<Character> defaultCharacters = DefaultGameData.getDefaultCharacters();
            for (Character character : defaultCharacters) {
                character = characterService.save(character);
                gameService.addCharacterToGame(game.getId(), character);
            }
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

    @Getter
    @NoArgsConstructor
    @Schema(hidden = true)
    public static class GameRequest {
        private String name;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @Schema(hidden = true)
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
            private final Integer id;
            private final String name;
            private final String description;
            private final UserController.UserResponse gameMaster;
            private final List<CharacterController.CharacterResponse.CharacterData> characters;

            public GameData(Game game) {
                this.id = game.getId();
                this.name = game.getName();
                this.description = game.getDescription();
                this.gameMaster = new UserController.UserResponse(game.getGameMaster());
                this.characters = game.getCharacters().stream().map(CharacterController.CharacterResponse.CharacterData::new).toList();
            }
        }
    }

}
