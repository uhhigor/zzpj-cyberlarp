package com.example.cyberlarpapi.game.model.chat.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private Integer id;
    private String content;
    private Integer senderId;
    private Integer groupChatId;


    public ChatMessageDTO(Integer id, String content, Integer senderId, Integer groupChatId) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.groupChatId = groupChatId;
    }

}
