package com.example.cyberlarpapi.game.model.task.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskRequest {
    private String name;
    private String description;
    private String type;
    private String location;
    private Float reward;
}
