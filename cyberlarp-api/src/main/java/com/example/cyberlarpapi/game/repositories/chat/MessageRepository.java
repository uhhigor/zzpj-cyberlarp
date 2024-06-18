package com.example.cyberlarpapi.game.repositories.chat;

import com.example.cyberlarpapi.game.model.chat.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
