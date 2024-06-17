package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.CharacterAlreadyInGroupException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.ErrorResponse;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.chat.DTO.AcceptInvitationRequest;
import com.example.cyberlarpapi.game.model.chat.DTO.ChatMessageDTO;
import com.example.cyberlarpapi.game.model.chat.DTO.InviteUserRequest;
import com.example.cyberlarpapi.game.model.chat.GroupChat;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.GroupChatService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Chat Operations", description = "Operations related to group chat in specific game")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/{gameId}/chat")
public class ChatController {
    private final GroupChatService groupChatService;
    private final GameService gameService;
    private final UserService userService;

    @ExceptionHandler({NotFoundException.class, CharacterAlreadyInGroupException.class, InvalidFactionException.class})
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Create a new group chat", description = "Create a new group chat in the game")
    @PostMapping("/")
    public ResponseEntity<GroupChat> createGroupChat(@PathVariable Integer gameId) {
        try {
            _User user = userService.getCurrentUser();
            Game game = gameService.getById(gameId);
            Character character = game.getUserCharacter(user);
            GroupChat groupChat = groupChatService.createGroupChat(game, character);
            return new ResponseEntity<>(groupChat, HttpStatus.CREATED);
        } catch (NotFoundException | GameNotFoundException | CharacterNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Invite user to group chat", description = "Invite user to group chat in the game by providing group id and character id")
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<ErrorResponse> inviteUserToGroupChat(@PathVariable Integer groupId, @RequestBody InviteUserRequest inviteUserRequest, @PathVariable String gameId) {
        try {
            groupChatService.inviteUserToGroupChat(groupId, inviteUserRequest.getCharacterId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CharacterAlreadyInGroupException | InvalidFactionException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Accept invitation to group chat", description = "Accept invitation to group chat in the game by providing group id and character id")
    @PostMapping("/{groupId}/accept")
    public ResponseEntity<Void> acceptInvitationToGroupChat(@PathVariable Integer groupId, @RequestBody AcceptInvitationRequest acceptInvitationRequest, @PathVariable String gameId) {
        try {
            groupChatService.acceptInvitationToGroupChat(groupId, acceptInvitationRequest.getCharacterId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Send message to group chat", description = "Send message to group chat in the game by providing group id and message content")
    @PostMapping("/{groupId}/message")
    public ResponseEntity<Void> addMessageToGroupChat(@PathVariable Integer groupId, @RequestBody ChatMessageDTO messageDTO, @PathVariable String gameId) {
        try {
            groupChatService.addMessageToGroupChat(groupId, messageDTO.getContent(), messageDTO.getSenderId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get messages from group chat", description = "Get messages from group chat in the game by providing group id")
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<Map<String, String>>> getMessagesFromGroupChat(@PathVariable Integer groupId, @PathVariable String gameId) throws NotFoundException {
        List<ChatMessageDTO> messages = groupChatService.getMessagesFromGroupChat(groupId);
        if (messages == null) {
            throw new NotFoundException("Messages not found in group with id: " + groupId);
        }
        List<Map<String, String>> response = messages.stream()
                .map(message -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("content", message.getContent());
                    map.put("senderId", message.getSenderId().toString());
                    return map;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Remove old messages", description = "Remove old messages from group chat in the game by providing group id")
    @DeleteMapping("/{groupId}/messages")
    public ResponseEntity<Void> removeOldMessages(@PathVariable Integer groupId, @PathVariable String gameId) {
        groupChatService.removeOldMessages();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Check if user has access to group chat", description = "Check if user has access to group chat in the game by providing group id and character id")
    @GetMapping("/{groupId}/access")
    public ResponseEntity<Boolean> hasAccess(@PathVariable Integer groupId, @RequestParam Integer characterId, @PathVariable String gameId) {
        try {
            return new ResponseEntity<>(groupChatService.hasAccess(groupId, characterId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Check if user is owner of group chat", description = "Check if user is owner of group chat in the game by providing group id and character id")
    @GetMapping("/{groupId}/owner")
    public ResponseEntity<Boolean> isOwner(@PathVariable Integer groupId, @RequestParam Integer characterId, @PathVariable String gameId) {
        try {
            return new ResponseEntity<>(groupChatService.isOwner(groupId, characterId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get group chat", description = "Get group chat in the game by providing group id")
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupChat> getGroupChat(@PathVariable Integer groupId, @PathVariable String gameId) {
        try {
            return new ResponseEntity<>(groupChatService.getGroupChat(groupId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
