package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.UserRepository;
import com.example.cyberlarpapi.game.model.chat.DTO.ChatMessageDTO;
import com.example.cyberlarpapi.game.model.chat.DTO.GroupChatRequest;
import com.example.cyberlarpapi.game.model.chat.GroupChat;
import com.example.cyberlarpapi.game.model.chat.Role;
import com.example.cyberlarpapi.game.model.chat.message.ChatMessage;
import com.example.cyberlarpapi.game.repositories.chat.ChatMessageRepository;
import com.example.cyberlarpapi.game.repositories.chat.GroupChatRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ChatMessageDTO> getMessagesFromGroupChat(Integer groupId) throws NotFoundException {
        List<ChatMessage> messages = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId))
                .getMessages();
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDto(ChatMessage message) {
        return new ChatMessageDTO(message.getId(), message.getContent(),
                message.getSender().getId(), message.getGroupChat().getId());
    }
}