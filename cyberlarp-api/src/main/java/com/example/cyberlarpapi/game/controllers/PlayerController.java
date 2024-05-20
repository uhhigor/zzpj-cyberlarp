package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerException;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerNotFoundException;
import com.example.cyberlarpapi.game.exceptions.PlayerException.PlayerServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.services.PlayerService;
@Controller
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    private final UserService userService;

    private final GameService gameService;

    public PlayerController(PlayerService playerService, UserService userService, GameService gameService) {
        this.playerService = playerService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new PlayerResponse(playerService.getById(id)));
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePlayer(@PathVariable Integer id) {
        try {
            playerService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@RequestBody PlayerRequest request) {
        try {
            User user = userService.getUserById(request.getUserId());
            Game game = gameService.getById(request.getGameId());
            Player player = Player.builder()
                    .user(user)
                    .game(game)
                    .build(); // Create player

            player = playerService.save(player); // Save player

            user.addPlayer(player); // Add player to user
            game.addPlayer(player); // Add player to game
            gameService.save(game); // Update game
            userService.save(user); // Update user
            return ResponseEntity.ok(new PlayerResponse("Player created successfully", player));
        } catch (PlayerException  e) {
            return ResponseEntity.badRequest().body(new PlayerResponse(e.getMessage()));
        } catch (GameNotFoundException | UserServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PlayerRequest {
        private Integer userId;
        private Integer gameId;
    }

    @Getter
    public static class PlayerResponse {
        private String message;
        private PlayerData player;

        public PlayerResponse(String message, Player player) {
            this.message = message;
            this.player = new PlayerData(player);
        }

        public PlayerResponse(Player player) {
            this.player = new PlayerData(player);
        }

        public PlayerResponse(String message) {
            this.message = message;
        }

        public static class PlayerData {
            private Integer id;
            private Integer userId;
            private Integer gameId;

            public PlayerData(Player player) {
                this.id = player.getId();
                this.userId = player.getUser().getId();
                this.gameId = player.getGame().getId();
            }
        }
    }
}
