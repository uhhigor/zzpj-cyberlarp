package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.model.chat.message.BroadcastMessage;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.repositories.chat.BroadcastMessageRepository;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastMessageService {

    private final BroadcastMessageRepository broadcastMessageRepository;
    private final CharacterRepository characterRepository;

    @Transactional
    public BroadcastMessage sendBroadcastMessage(Integer senderId, String scope, String content) throws NotFoundException {
        Character sender = characterRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Character not found with id: " + senderId));
        BroadcastMessage message = new BroadcastMessage(sender, scope, content);
        return broadcastMessageRepository.save(message);
    }

    public List<BroadcastMessage> getBroadcastMessages(String scope) {
        return broadcastMessageRepository.findByScopeOrScope(scope, "ALL");
    }
}
