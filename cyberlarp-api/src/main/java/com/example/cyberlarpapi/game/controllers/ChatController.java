package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.ChatExceptions.CharacterAlreadyInGroupException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.ErrorResponse;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.model.chat.DTO.AcceptInvitationRequest;
import com.example.cyberlarpapi.game.model.chat.DTO.ChatMessageDTO;
import com.example.cyberlarpapi.game.model.chat.DTO.GroupChatRequest;
import com.example.cyberlarpapi.game.model.chat.DTO.InviteUserRequest;
import com.example.cyberlarpapi.game.model.chat.GroupChat;
import com.example.cyberlarpapi.game.services.GroupChatService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groupChat")
public class ChatController {
    private final GroupChatService groupChatService;

    @ExceptionHandler({NotFoundException.class, CharacterAlreadyInGroupException.class, InvalidFactionException.class})
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatRequest chatRequest) {
        try {
            GroupChat groupChat = groupChatService.createGroupChat(chatRequest);
            return new ResponseEntity<>(groupChat, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{groupId}/invite")
    public ResponseEntity<ErrorResponse> inviteUserToGroupChat(@PathVariable Integer groupId, @RequestBody InviteUserRequest inviteUserRequest) {
        try {
            groupChatService.inviteUserToGroupChat(groupId, inviteUserRequest.getCharacterId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CharacterAlreadyInGroupException | InvalidFactionException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{groupId}/accept")
    public ResponseEntity<Void> acceptInvitationToGroupChat(@PathVariable Integer groupId, @RequestBody AcceptInvitationRequest acceptInvitationRequest) {
        try {
            groupChatService.acceptInvitationToGroupChat(groupId, acceptInvitationRequest.getCharacterId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{groupId}/message")
    public ResponseEntity<Void> addMessageToGroupChat(@PathVariable Integer groupId, @RequestBody ChatMessageDTO messageDTO) {
        try {
            groupChatService.addMessageToGroupChat(groupId, messageDTO.getContent(), messageDTO.getSenderId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<Map<String, String>>> getMessagesFromGroupChat(@PathVariable Integer groupId) throws NotFoundException {
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

    @DeleteMapping("/{groupId}/messages")
    public ResponseEntity<Void> removeOldMessages(@PathVariable Integer groupId) throws NotFoundException {
        groupChatService.removeOldMessages();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{groupId}/access")
    public ResponseEntity<Boolean> hasAccess(@PathVariable Integer groupId, @RequestParam Integer characterId) {
        try {
            return new ResponseEntity<>(groupChatService.hasAccess(groupId, characterId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{groupId}/owner")
    public ResponseEntity<Boolean> isOwner(@PathVariable Integer groupId, @RequestParam Integer characterId) {
        try {
            return new ResponseEntity<>(groupChatService.isOwner(groupId, characterId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupChat> getGroupChat(@PathVariable Integer groupId) {
        try {
            return new ResponseEntity<>(groupChatService.getGroupChat(groupId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
