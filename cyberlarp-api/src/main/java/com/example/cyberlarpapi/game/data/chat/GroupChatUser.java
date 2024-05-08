package com.example.cyberlarpapi.game.data.chat;

import com.example.cyberlarpapi.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class GroupChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupChat groupChat;

    private Role role;

    public GroupChatUser(User user, GroupChat groupChat, Role role) {
        this.user = user;
        this.groupChat = groupChat;
        this.role = role;
    }
}
