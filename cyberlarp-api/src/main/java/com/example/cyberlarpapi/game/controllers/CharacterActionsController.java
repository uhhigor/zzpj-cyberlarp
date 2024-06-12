package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.model.character.Attribute;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/action")
public class CharacterActionsController {
    private final CharacterService characterService;
    public CharacterActionsController(CharacterService characterService) {
        this.characterService = characterService;
    }

    public record RollAttributeRequest(Integer characterId) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RollAttributeResponse(String message, Integer result) {
    }
    @PostMapping("/roll/{attribute}")
    public ResponseEntity<RollAttributeResponse> roll(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable String attribute,
                                                      @RequestBody RollAttributeRequest request) {

        if(userDetails == null) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("Not logged in", null));
        }
        if(request.characterId == null) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("Character ID is required", null));
        }

        Attribute attributeEnum;
        try {
            attributeEnum = Attribute.valueOf(attribute.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("Invalid attribute", null));
        }
        Character character;
        try {
            character = characterService.getById(request.characterId);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if(!Objects.equals(character.getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new RollAttributeResponse("Not your character", null));
        }
        return ResponseEntity.ok(new RollAttributeResponse(null, character.rollAttributeCheck(attributeEnum)));
    }
}
