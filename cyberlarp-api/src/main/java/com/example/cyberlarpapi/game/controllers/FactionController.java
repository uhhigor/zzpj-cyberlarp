package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.FactionException.FactionNotFoundException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionServiceException;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.character.faction.FactionDTO;
import com.example.cyberlarpapi.game.services.FactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Faction Operations", description = "Operations related to factions in specific game")
@RestController
@RequestMapping("/factions")
public class FactionController {

    private final FactionService factionService;

    public FactionController(FactionService factionService) {
        this.factionService = factionService;
    }

    @Operation(summary = "Get faction by id", description = "Get faction by id")
    @GetMapping("/{id}")
    public ResponseEntity<Faction> getFactionById(@PathVariable int id) {
        try {
            Faction faction = factionService.getById(id);
            return ResponseEntity.ok(faction);
        } catch (FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all factions", description = "Get all factions")
    @GetMapping
    public List<Faction> getAllFactions() {
        return factionService.getAll();
    }

    @Operation(summary = "Create a new faction", description = "Create a new faction in the game, providing name and description")
    @PostMapping
    public ResponseEntity<Faction> createFaction(@RequestBody FactionDTO factionDTO) throws FactionServiceException {
        Faction createdFaction = factionService.create(factionDTO);
        return new ResponseEntity<>(createdFaction, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a faction", description = "Update a faction in the game, providing id, name and description")
    @PutMapping("/{id}")
    public ResponseEntity<Faction> updateFaction(@PathVariable int id, @RequestBody FactionDTO factionDTO) {
        try {
            Faction updatedFaction = factionService.update(id, factionDTO);
            return ResponseEntity.ok(updatedFaction);
        } catch (FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a faction", description = "Delete a faction in the game by providing id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaction(@PathVariable int id) {
        factionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
