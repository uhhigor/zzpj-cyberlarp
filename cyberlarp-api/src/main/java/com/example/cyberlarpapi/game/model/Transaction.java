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

    private String senderAccount;

    private String receiverAccount;

    private int amount;

    private LocalDateTime timestamp;

    public Transaction(String senderAccount, String receiverAccount, int amount, LocalDateTime timestamp) {
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Transaction() {

    }
}
