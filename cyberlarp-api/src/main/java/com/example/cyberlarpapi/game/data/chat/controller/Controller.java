package com.example.cyberlarpapi.game.data.chat.controller;

import com.example.cyberlarpapi.game.data.chat.DTO.GroupChatRequest;
import com.example.cyberlarpapi.game.data.chat.DTO.InviteUserRequest;
import com.example.cyberlarpapi.game.data.chat.DTO.AcceptInvitationRequest;
import com.example.cyberlarpapi.game.data.chat.GroupChat;
import com.example.cyberlarpapi.game.data.chat.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/group")
public class Controller {
    private final GroupChatService groupChatService;

    @Autowired
    public Controller(GroupChatService groupChatService) {
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
    public ResponseEntity<Void> addMessageToGroupChat(@PathVariable Integer groupId, @RequestBody String content, @RequestParam Integer senderId) {
        try {
            groupChatService.addMessageToGroupChat(groupId, content, senderId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
