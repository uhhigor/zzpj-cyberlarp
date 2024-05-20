package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.PlayerService;
import com.example.cyberlarpapi.game.services.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.model.character.Character;

import java.util.List;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    private final PlayerService playerService;

    private final UserService userService;

    public GameController(GameService gameService, PlayerService playerService, UserService userService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@RequestBody GameRequest request) {
        try {
            User gameMaster = userService.getUserById(request.getGameMasterUserId());
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



    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new GameResponse(gameService.getById(id)));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
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
                this.playerIds = game.getPlayers().stream().map(Player::getId).toList();
                this.availableCharacterIds = game.getAvailableCharacters().stream().map(Character::getId).toList();
            }
        }
    }

}
