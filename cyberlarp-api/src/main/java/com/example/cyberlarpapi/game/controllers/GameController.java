package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.DefaultGameData;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Create a new game [USER]", description = "Create a new game in the system, providing name, description and game master user id")
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

    @Operation(summary = "Get all games [USER]", description = "Get all games in the system")
    @GetMapping
    public ResponseEntity<List<ShortGameResponse>> getAllGames() {
        List<Game> games = gameService.getAll();
        return ResponseEntity.ok(games.stream().map(ShortGameResponse::new).toList());
    }


    @Operation(summary = "Get game by id [GM]", description = "Get game by id")
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Integer id) {
        try {
            _User user = userService.getCurrentUser();
            Game game = gameService.getById(id);
            if (!game.getGameMaster().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(new GameResponse(game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update game [GM]", description = "Update game data by id, providing name and description")
    @PutMapping("/{id}/textData")
    public ResponseEntity<GameResponse> updateGameTextDataById(@PathVariable Integer id, @RequestBody GameRequest request) {
        try {
            _User user = userService.getCurrentUser();
            Game game = gameService.getById(id);
            if (!game.getGameMaster().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            game.setName(request.getName());
            game.setDescription(request.getDescription());
            game = gameService.save(game);
            return ResponseEntity.ok(new GameResponse("Game updated successfully", game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete game [GM]", description = "Delete game by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<GameResponse> deleteGame(@PathVariable Integer id) {
        try {
            _User user = userService.getCurrentUser();
            Game game = gameService.getById(id);
            if (!game.getGameMaster().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            gameService.deleteById(id);
            return ResponseEntity.ok(new GameResponse("Game deleted successfully"));
        } catch (GameServiceException | UserServiceException e) {
            return ResponseEntity.badRequest().build();
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
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
    public static class ShortGameResponse {
        private final Integer id;
        private final String name;
        private final String description;
        private final Integer gameMasterId;

        public ShortGameResponse(Game game) {
            this.id = game.getId();
            this.name = game.getName();
            this.description = game.getDescription();
            this.gameMasterId = game.getGameMaster().getId();
        }
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
