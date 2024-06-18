package com.example.cyberlarpapi.game.model.chat.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BroadcastMessageDTO {
    private Integer id;
    private String content;
    private Integer senderId;
    private String scope;

    public BroadcastMessageDTO(Integer id, String content, Integer senderId, String scope) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.scope = scope;
    }
}
