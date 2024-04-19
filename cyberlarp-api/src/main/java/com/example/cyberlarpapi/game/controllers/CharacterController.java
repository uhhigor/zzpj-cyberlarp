package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.data.character.CharacterDTO;
import com.example.cyberlarpapi.game.exceptions.CharacterServiceException;
import com.example.cyberlarpapi.game.services.CharacterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCharacterById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(characterService.getById(id));
        } catch (CharacterServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> updateCharacter(@PathVariable Integer id, @RequestBody CharacterDTO characterDTO) {
        try {
            return ResponseEntity.ok(characterService.update(id, characterDTO));
        } catch (CharacterServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCharacter(@PathVariable Integer id) {
        characterService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Object> createCharacter(@RequestBody CharacterDTO characterDTO) {
        try {
            return ResponseEntity.ok(characterService.createCharacter(characterDTO));
        } catch (CharacterServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
