package com.example.cyberlarpapi.game.controllers;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.MessageNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.chat.SCOPE;
import com.example.cyberlarpapi.game.model.chat.message.Message;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Message Operations", description = "Operations related to messages in the system")
@RestController
@RequestMapping("/game/{gameId}/message")
@RequiredArgsConstructor
public class MessageController {

    private final GameService gameService;

    private final UserService userService;

    @Operation(summary = "Send message in game", description = "Send message in game by providing game id, content and scope")
    @PostMapping("/")
    public ResponseEntity<String> addMessageToGame(@PathVariable Integer gameId, @RequestBody MessageRequest messageRequest) {
        try {
            Game game = gameService.getById(gameId);
            _User user = userService.getCurrentUser();
            Character character = game.getUserCharacter(user);
            String content = messageRequest.content;
            SCOPE scope = messageRequest.scope;
            Message message = new Message(character, scope, content);
            gameService.addMessageToGame(gameId, message);
            return ResponseEntity.ok("Message " + message.getContent() + " sent in game " + game.getName());
        } catch (GameNotFoundException | MessageNotFoundException | CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserServiceException e) {
           return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get messages from game", description = "Get messages from game by providing game id and scope")
    @GetMapping("/{scope}")
    public ResponseEntity<List<String>> getMessagesFromGame(@PathVariable Integer gameId, @PathVariable SCOPE scope) {
        try {
            Game game = gameService.getById(gameId);
            _User user = userService.getCurrentUser();
            Character character = game.getUserCharacter(user);
            List<Message> messages = gameService.getMessagesFromGame(gameId, character, scope);
            List<String> messageContents = new ArrayList<>();
            for (Message message : messages) {
                messageContents.add(message.getTimestamp() + " [" + message.getScope() + "] " + message.getSender().getName() + ": " + message.getContent());
            }
            return ResponseEntity.ok(messageContents);
        } catch (CharacterNotFoundException | GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidFactionException | UserServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class MessageResponse {
        private final String message;

        public MessageResponse(String message) {
            this.message = message;
        }

    }


    public static class MessageRequest {
        public String content;
        public SCOPE scope;

    }
}
