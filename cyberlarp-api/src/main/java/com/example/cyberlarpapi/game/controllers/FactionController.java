package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.FactionException.FactionNotFoundException;
import com.example.cyberlarpapi.game.exceptions.FactionException.FactionServiceException;
import com.example.cyberlarpapi.game.model.character.faction.Faction;
import com.example.cyberlarpapi.game.model.character.faction.FactionDTO;
import com.example.cyberlarpapi.game.services.FactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/factions")
public class FactionController {

    private final FactionService factionService;

    public FactionController(FactionService factionService) {
        this.factionService = factionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faction> getFactionById(@PathVariable int id) {
        try {
            Faction faction = factionService.getById(id);
            return ResponseEntity.ok(faction);
        } catch (FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Faction> getAllFactions() {
        return factionService.getAll();
    }

    @PostMapping
    public ResponseEntity<Faction> createFaction(@RequestBody FactionDTO factionDTO) throws FactionServiceException {
        Faction createdFaction = factionService.create(factionDTO);
        return new ResponseEntity<>(createdFaction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faction> updateFaction(@PathVariable int id, @RequestBody FactionDTO factionDTO) {
        try {
            Faction updatedFaction = factionService.update(id, factionDTO);
            return ResponseEntity.ok(updatedFaction);
        } catch (FactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaction(@PathVariable int id) {
        factionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
