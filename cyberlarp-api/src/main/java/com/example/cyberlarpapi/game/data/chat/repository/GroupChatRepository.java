package com.example.cyberlarpapi.game.data.chat.repository;

import com.example.cyberlarpapi.game.data.chat.GroupChat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatRepository extends CrudRepository<GroupChat, Integer> {

}
