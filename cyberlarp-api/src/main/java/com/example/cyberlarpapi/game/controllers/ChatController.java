package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.model.chat.DTO.AcceptInvitationRequest;
import com.example.cyberlarpapi.game.model.chat.DTO.ChatMessageDTO;
import com.example.cyberlarpapi.game.model.chat.DTO.GroupChatRequest;
import com.example.cyberlarpapi.game.model.chat.DTO.InviteUserRequest;
import com.example.cyberlarpapi.game.model.chat.GroupChat;
import com.example.cyberlarpapi.game.services.GroupChatService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groupChat")
public class ChatController {
    private final GroupChatService groupChatService;

    @Autowired
    public ChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @PostMapping
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatRequest chatRequest) {
        GroupChat groupChat = groupChatService.createGroupChat(chatRequest);
        return new ResponseEntity<>(groupChat, HttpStatus.CREATED);
    }

    @PostMapping("/{groupId}/invite")
    public ResponseEntity<Void> inviteUserToGroupChat(@PathVariable Integer groupId, @RequestBody InviteUserRequest inviteUserRequest) {
        try {
            groupChatService.inviteUserToGroupChat(groupId, inviteUserRequest.getUserId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{groupId}/accept")
    public ResponseEntity<Void> acceptInvitationToGroupChat(@PathVariable Integer groupId, @RequestBody AcceptInvitationRequest acceptInvitationRequest) {
        try {
            groupChatService.acceptInvitationToGroupChat(groupId, acceptInvitationRequest.getUserId());
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
        List<Map<String, String>> response = messages.stream()
                .map(message -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("content", message.getContent());
                    map.put("senderId", message.getSenderId().toString());
                    return map;
                })
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}/messages")
    public ResponseEntity<Void> removeOldMessages(@PathVariable Integer groupId) {
        groupChatService.removeOldMessages();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
