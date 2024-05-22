package com.example.cyberlarpapi.game.model.chat;

import com.example.cyberlarpapi.game.exceptions.ChatExceptions.CharacterAlreadyInGroupException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.chat.message.ChatMessage;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id")
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id")
    private List<GroupChatCharacter> characters = new ArrayList<>();

    @ManyToOne
    private Game game;

    @ManyToOne
    private Character owner;


    public void inviteCharacter(Character character, Role role) {
        if (characters.stream().anyMatch(gc -> gc.getCharacter().equals(character))) {
            throw new CharacterAlreadyInGroupException("Character already in group chat");
        }

        if (owner.getFaction() != null && !owner.getFaction().equals(character.getFaction())) {
            throw new InvalidFactionException("Character does not belong to the same faction as the owner");
        }

        GroupChatCharacter groupChatCharacter = new GroupChatCharacter(character, this, role);
        characters.add(groupChatCharacter);
    }

    public boolean hasCharacter(Character character) {
        return characters.stream().anyMatch(gc -> gc.getCharacter().equals(character));
    }

    public void acceptInvitation(Character character) {
        for (GroupChatCharacter groupChatCharacter : characters) {
            if (groupChatCharacter.getCharacter().equals(character)) {
                groupChatCharacter.setRole(Role.MEMBER);
            }
        }
    }

    public boolean hasAccess(Character character) {
        for (GroupChatCharacter groupChatCharacter : characters) {
            if (groupChatCharacter.getCharacter().equals(character) && groupChatCharacter.getRole().equals(Role.MEMBER)){
                return true;
            }
        }
        return false;
    }

    public boolean isOwner(Character character) {
        for (GroupChatCharacter groupChatCharacter : characters) {
            if (groupChatCharacter.getCharacter().equals(character) && groupChatCharacter.getRole().equals(Role.OWNER)){
                return true;
            }
        }
        return false;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

}