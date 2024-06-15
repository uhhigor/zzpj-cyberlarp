package com.example.cyberlarpapi.game.model.chat;

import com.example.cyberlarpapi.game.exceptions.ChatExceptions.CharacterAlreadyInGroupException;
import com.example.cyberlarpapi.game.exceptions.ChatExceptions.InvalidFactionException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.chat.message.ChatMessage;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<Character> characters = new ArrayList<>();

    @ManyToOne
    private Game game;

    @ManyToOne
    private Character owner;

    @Transient
    private List<Character> invitations = new ArrayList<>();

    @Transient
    private List<CharacterRole> characterRoles = new ArrayList<>();

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
        return characters.contains(character);
    }

    public void acceptInvitation(Character character) {
        if (invitations.contains(character)) {
            characters.add(character);
            characterRoles.add(new CharacterRole(character, Role.MEMBER));
            invitations.remove(character);
        } else {
            throw new InvalidFactionException("Character does not have an invitation to this group chat");
        }
    }

    public boolean hasAccess(Character character) {
        return characterRoles.stream()
                .anyMatch(cr -> cr.character.equals(character) && cr.role.equals(Role.MEMBER));
    }

    public boolean isOwner(Character character) {
        return character.equals(owner);
    }

    @Transactional
    public void addMessage(ChatMessage message) {
        try {
            messages.add(message);
        } catch (Exception e) {
            throw new RuntimeException("Could not add message to group chat");
        }
    }

    @Override
    public String toString() {
        return "GroupChat{" +
                "id=" + id +
                ", messages=" + messages.size() +
                ", characters=" + characters.size() +
                ", game=" + game.getId() +
                ", owner=" + owner.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChat groupChat = (GroupChat) o;
        return Objects.equals(id, groupChat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterRole {
        private Character character;
        private Role role;
    }
}
