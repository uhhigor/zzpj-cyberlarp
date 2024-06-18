package com.example.cyberlarpapi.game.model.chat.DTO;

import com.example.cyberlarpapi.game.model.chat.SCOPE;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private Integer id;
    private Integer senderId;
    private SCOPE scope;
    private String content;

    public MessageDTO(Integer id, Integer senderId, SCOPE scope, String content) {
        this.id = id;
        this.senderId = senderId;
        this.scope = scope;
        this.content = content;
    }
}
