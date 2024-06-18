package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.ErrorResponse;
import com.example.cyberlarpapi.game.model.chat.DTO.BroadcastMessageDTO;
import com.example.cyberlarpapi.game.model.chat.message.BroadcastMessage;
import com.example.cyberlarpapi.game.services.BroadcastMessageService;
import com.example.cyberlarpapi.game.services.CharacterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Broadcast Operations", description = "Operations related to broadcast messages")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/{gameId}/broadcast")
public class BroadcastController {

    private final BroadcastMessageService broadcastMessageService;
    private final CharacterService characterService;

    @ExceptionHandler({NotFoundException.class, CharacterNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Send a broadcast message", description = "Send a broadcast message to a specific scope or to all players")
    @PostMapping("/send")
    public ResponseEntity<Void> sendBroadcastMessage(@RequestBody BroadcastMessageDTO messageDTO, @PathVariable Integer gameId) {
        try {
            broadcastMessageService.sendBroadcastMessage(messageDTO.getSenderId(), messageDTO.getScope(), messageDTO.getContent());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get broadcast messages", description = "Get broadcast messages for a specific scope")
    @GetMapping("/messages")
    public ResponseEntity<List<BroadcastMessageDTO>> getBroadcastMessages(@RequestParam String scope, @PathVariable Integer gameId) {
        List<BroadcastMessage> messages = broadcastMessageService.getBroadcastMessages(scope);
        List<BroadcastMessageDTO> response = messages.stream()
                .map(message -> new BroadcastMessageDTO(message.getId(), message.getContent(), message.getSender().getId(), message.getScope()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
