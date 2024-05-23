package com.example.cyberlarpapi.game.model;

import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private Character sender;

    @ManyToOne
    private Character receiver;

    private int amount;

    private LocalDateTime timestamp;

    public Transaction(Character sender, Character receiver, int amount, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Transaction() {

    }
}
