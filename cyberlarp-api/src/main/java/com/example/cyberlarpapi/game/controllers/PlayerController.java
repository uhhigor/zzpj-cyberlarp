package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.PlayerServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
