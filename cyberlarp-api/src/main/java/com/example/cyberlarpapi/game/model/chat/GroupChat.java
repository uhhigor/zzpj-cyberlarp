package com.example.cyberlarpapi.game.model.chat;

import com.example.cyberlarpapi.game.exceptions.ChatExceptions.CharacterAlreadyInGroupException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.model.game.Game;
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

    @Transient
    private List<Character> invitations = new ArrayList<>();

    public void inviteCharacter(Character character) {
        if (hasCharacter(character)) {
            throw new CharacterAlreadyInGroupException("Character already in group chat");
        }

        if (owner.getFaction() != null && !owner.getFaction().equals(character.getFaction())) {
            throw new InvalidFactionException("Character does not belong to the same faction as the owner");
        }

        invitations.add(character);
    }

    public boolean hasCharacter(Character character) {
        return characters.stream().anyMatch(gc -> gc.getCharacter().equals(character));
    }

    public void acceptInvitation(Character character) {
        if (invitations.contains(character)) {
            GroupChatCharacter groupChatCharacter = new GroupChatCharacter(character, this, Role.MEMBER);
            characters.add(groupChatCharacter);
            invitations.remove(character);
        }
    }

    public boolean hasAccess(Character character) {
        return characters.stream().anyMatch(gc -> gc.getCharacter().equals(character) && gc.getRole().equals(Role.MEMBER));
    }

    public boolean isOwner(Character character) {
        return characters.stream().anyMatch(gc -> gc.getCharacter().equals(character) && gc.getRole().equals(Role.OWNER));
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }
}
