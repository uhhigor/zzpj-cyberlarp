package com.example.cyberlarpapi.game.data.chat.repository;

import com.example.cyberlarpapi.game.data.chat.messege.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    void deleteByTimestampBefore(LocalDateTime thresholdDate);
}
