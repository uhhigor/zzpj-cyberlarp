package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.model.chat.message.Message;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.repositories.chat.MessageRepository;
import com.example.cyberlarpapi.game.repositories.character.CharacterRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional
    public Message getMessageById(Integer id) throws NotFoundException {
        return messageRepository.findById(id).orElseThrow(() -> new NotFoundException("Message not found"));
    }


}
