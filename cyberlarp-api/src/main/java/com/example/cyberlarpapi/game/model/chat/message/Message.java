package com.example.cyberlarpapi.game.model.chat.message;

import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.chat.SCOPE;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    private Character sender;

    @Column(nullable = false)
    private SCOPE scope;

    @Column(nullable = false)
    private String content;

    public Message(Character sender, SCOPE scope, String content) {
        this.timestamp = LocalDateTime.now();
        this.sender = sender;
        this.scope = scope;
        this.content = content;
    }

    public Message(Integer id, Character sender, SCOPE scope, String content) {
        this.id = id;
        this.timestamp = LocalDateTime.now();
        this.sender = sender;
        this.scope = scope;
        this.content = content;
    }
}
