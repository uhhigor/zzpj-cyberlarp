package com.example.cyberlarpapi.game.model.chat.message;

import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    private Character sender;

    @Column(nullable = false)
    private String scope;

    @Column(nullable = false)
    private String content;

    public BroadcastMessage(Character sender, String scope, String content) {
        this.timestamp = LocalDateTime.now();
        this.sender = sender;
        this.scope = scope;
        this.content = content;
    }
}
