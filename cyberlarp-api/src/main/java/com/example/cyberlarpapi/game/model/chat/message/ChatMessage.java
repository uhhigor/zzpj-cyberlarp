package com.example.cyberlarpapi.game.model.chat.message;

import com.example.cyberlarpapi.game.model.character.Character;
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
    private Integer id;

    private String content;

    @ManyToOne
    private Character sender;

    private LocalDateTime timestamp;

    public ChatMessage(String content, Character sender) {
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }
}