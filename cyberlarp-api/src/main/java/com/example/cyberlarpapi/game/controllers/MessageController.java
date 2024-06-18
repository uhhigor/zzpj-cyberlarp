package com.example.cyberlarpapi.game.controllers;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.MessageNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.chat.SCOPE;
import com.example.cyberlarpapi.game.model.chat.message.Message;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.MessageService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "Message Operations", description = "Operations related to messages in the system")
@RestController
@RequestMapping("/game/{gameId}/message")
@RequiredArgsConstructor
public class MessageController {

    private final GameService gameService;

    private final UserService userService;

    private final CharacterService characterService;

    private final MessageService messageService;

    @Operation(summary = "Add message to game", description = "Add message to game by providing game id and content")
    @PutMapping("/{content}/{scope}/{characterId}")
    public ResponseEntity<GameController.GameResponse> addMessageToGame(@PathVariable Integer gameId, @PathVariable String content, @PathVariable SCOPE scope, @PathVariable Integer characterId) {
        try {
            Character character = characterService.getById(characterId);
            if(!Objects.equals(character.getUser().getId(), userService.getCurrentUser().getId())) {
                return ResponseEntity.badRequest().build();
            }
            Message message = new Message(character, scope, content);
            gameService.addMessageToGame(gameId, message);
            return ResponseEntity.ok(new GameController.GameResponse("Message added to game successfully", gameService.getById(gameId)));
        } catch (GameNotFoundException | MessageNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CharacterNotFoundException | UserServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Delete message from game", description = "Delete message from game by providing game id and message id")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<GameController.GameResponse> deleteMessageFromGame(@PathVariable Integer gameId, @PathVariable Integer messageId) {
        try {
            Message message = messageService.getMessageById(messageId);
            gameService.deleteMessageFromGame(gameId, message);
            return ResponseEntity.ok(new GameController.GameResponse("Message deleted from game successfully", gameService.getById(gameId)));
        } catch (GameNotFoundException | MessageNotFoundException | NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get messages by game id, character id and scope", description = "Get messages by game id, character id and scope")
    @GetMapping("/{characterId}/{scope}")
    public ResponseEntity<List<Message>> getMessagesFromGame(@PathVariable Integer gameId, @PathVariable Integer characterId, @PathVariable SCOPE scope) {
        try {
            Character character = characterService.getById(characterId);
            return ResponseEntity.ok(gameService.getMessagesFromGame(gameId, character, scope));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidFactionException e) {
            throw new RuntimeException(e);
        }
    }

    class MessageResponse {
        private final String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
