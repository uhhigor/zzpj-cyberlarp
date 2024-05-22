package com.example.cyberlarpapi.game.model.chat;

import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class GroupChatCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupChat groupChat;

    private Role role;

    public GroupChatCharacter(Character character, GroupChat groupChat, Role role) {
        this.character = character;
        this.groupChat = groupChat;
        this.role = role;
    }
}