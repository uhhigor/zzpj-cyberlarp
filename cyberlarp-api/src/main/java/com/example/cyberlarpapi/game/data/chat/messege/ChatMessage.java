package com.example.cyberlarpapi.game.data.chat.messege;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.data.chat.GroupChat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupChat groupChat;

    private LocalDateTime timestamp;

    public ChatMessage(String content, User sender, GroupChat groupChat) {
        this.content = content;
        this.sender = sender;
        this.groupChat = groupChat;
        this.timestamp = LocalDateTime.now();
    }
}
