package com.example.cyberlarpapi.game.repositories.chat;

import com.example.cyberlarpapi.game.model.chat.GroupChat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatRepository extends CrudRepository<GroupChat, Integer> {

}