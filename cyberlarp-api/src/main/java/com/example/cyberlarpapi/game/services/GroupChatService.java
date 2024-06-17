package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.ChatExceptions.CharacterAlreadyInGroupException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.chat.DTO.ChatMessageDTO;
import com.example.cyberlarpapi.game.model.chat.GroupChat;
import com.example.cyberlarpapi.game.model.chat.message.ChatMessage;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.repositories.game.GameRepository;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import com.example.cyberlarpapi.game.repositories.chat.ChatMessageRepository;
import com.example.cyberlarpapi.game.repositories.chat.GroupChatRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CharacterRepository characterRepository;
    private final GameRepository gameRepository;

    public GroupChat createGroupChat(Game game, Character character) throws NotFoundException {

        GroupChat groupChat = new GroupChat();
        groupChat.setGame(game);
        groupChat.setOwner(character);
        groupChat = groupChatRepository.save(groupChat);
        game.getGroupChats().add(groupChat);
        gameRepository.save(game);
        return groupChat;
    }

    public void inviteUserToGroupChat(Integer groupId, Integer characterId) throws NotFoundException {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new NotFoundException("Character not found with id: " + characterId));

        if (groupChat.hasCharacter(character)) {
            throw new CharacterAlreadyInGroupException("Character already in group chat");
        }

        if (groupChat.getOwner().getFaction() != null && !groupChat.getOwner().getFaction().equals(character.getFaction())) {
            throw new InvalidFactionException("Character does not belong to the same faction as the owner");
        }

        groupChat.inviteCharacter(character);
        groupChatRepository.save(groupChat);
    }

    public void acceptInvitationToGroupChat(Integer groupId, Integer characterId) throws NotFoundException {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new NotFoundException("Character not found with id: " + characterId));

        groupChat.acceptInvitation(character);
        groupChatRepository.save(groupChat);
    }

    @Transactional
    public void addMessageToGroupChat(Integer groupId, String content, Integer senderId) throws NotFoundException {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
        Character sender = characterRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Character not found with id: " + senderId));
        ChatMessage chatMessage = new ChatMessage(content, sender);
        try {
            groupChat.addMessage(chatMessage);
        } catch (Exception e) {
            throw new RuntimeException("Could not add message to group chat");
        }
    }

    public void removeOldMessages() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(60);
        chatMessageRepository.deleteByTimestampBefore(thresholdDate);
    }


    public List<ChatMessageDTO> getMessagesFromGroupChat(Integer groupId) throws NotFoundException {
        List<ChatMessage> messages = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId))
                .getMessages();

        if (messages == null) {
            return null;
        }

        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDto(ChatMessage message) {
        return new ChatMessageDTO(message.getId(), message.getContent(),
                message.getSender().getId(), null);
    }

    public Boolean hasAccess(Integer groupId, Integer characterId) {
        GroupChat groupChat = groupChatRepository.findById(groupId).orElse(null);
        if (groupChat == null) {
            return false;
        }
        Character character = characterRepository.findById(characterId).orElse(null);
        if (character == null) {
            return false;
        }
        return groupChat.hasAccess(character);
    }

    public Boolean isOwner(Integer groupId, Integer characterId) {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElse(null);
        if (groupChat == null) {
            return false;
        }
        Character character = characterRepository.findById(characterId)
                .orElse(null);
        if (character == null) {
            return false;
        }
        return groupChat.isOwner(character);
    }

    public GroupChat getGroupChat(Integer groupId) throws NotFoundException {
        return groupChatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group chat not found with id: " + groupId));
    }
}