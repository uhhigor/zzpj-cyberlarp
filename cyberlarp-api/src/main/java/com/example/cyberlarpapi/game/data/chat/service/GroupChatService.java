package com.example.cyberlarpapi.game.data.chat.service;

import com.example.cyberlarpapi.game.data.chat.DTO.GroupChatRequest;
import com.example.cyberlarpapi.game.data.chat.GroupChat;
import com.example.cyberlarpapi.game.data.chat.GroupChatUser;
import com.example.cyberlarpapi.game.data.chat.Role;
import com.example.cyberlarpapi.game.data.chat.messege.ChatMessage;
import com.example.cyberlarpapi.game.data.chat.repository.GroupChatRepository;
import com.example.cyberlarpapi.game.data.chat.repository.ChatMessageRepository;
import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public GroupChatService(GroupChatRepository groupChatRepository, UserRepository userRepository, ChatMessageRepository chatMessageRepository) {
        this.groupChatRepository = groupChatRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public GroupChat createGroupChat(GroupChatRequest chatRequest) {
        GroupChat groupChat = new GroupChat();
        return groupChatRepository.save(groupChat);
    }

    public void inviteUserToGroupChat(Integer groupId, Integer userId) throws NotFoundException {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        groupChat.inviteUser(user, Role.MEMBER);
        groupChatRepository.save(groupChat);
    }

    public void acceptInvitationToGroupChat(Integer groupId, Integer userId) throws NotFoundException {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        groupChat.acceptInvitation(user);
        groupChatRepository.save(groupChat);
    }

    public void addMessageToGroupChat(Integer groupId, String content, Integer senderId) throws NotFoundException {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + senderId));
        ChatMessage chatMessage = new ChatMessage(content, sender, groupChat);
        chatMessageRepository.save(chatMessage);
    }

    public void removeOldMessages() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(60);
        chatMessageRepository.deleteByTimestampBefore(thresholdDate);
    }
}
