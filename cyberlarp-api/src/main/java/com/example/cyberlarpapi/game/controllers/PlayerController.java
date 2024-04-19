package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.data.PlayerDTO;
import com.example.cyberlarpapi.game.exceptions.PlayerServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.services.PlayerService;
@Controller
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPlayerById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(playerService.getById(id));
        } catch (PlayerServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePlayer(@PathVariable Integer id) {
        playerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Object> createPlayer(@RequestBody PlayerDTO playerDTO) {
        try {
            return ResponseEntity.ok(playerService.create(playerDTO));
        } catch (PlayerServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> updatePlayer(@PathVariable Integer id, @RequestBody PlayerDTO playerDTO) {
        try {
            return ResponseEntity.ok(playerService.update(id, playerDTO));
        } catch (PlayerServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
