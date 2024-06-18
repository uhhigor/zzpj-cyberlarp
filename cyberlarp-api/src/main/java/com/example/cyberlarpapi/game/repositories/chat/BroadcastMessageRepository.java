package com.example.cyberlarpapi.game.repositories.chat;

import com.example.cyberlarpapi.game.model.chat.message.BroadcastMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BroadcastMessageRepository extends JpaRepository<BroadcastMessage, Integer> {
    List<BroadcastMessage> findByScope(String scope);
    List<BroadcastMessage> findByScopeOrScope(String scope1, String scope2);
}
