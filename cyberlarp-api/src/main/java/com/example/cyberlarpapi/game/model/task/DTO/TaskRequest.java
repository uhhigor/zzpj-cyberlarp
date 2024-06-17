package com.example.cyberlarpapi.game.model.task.DTO;

import com.example.cyberlarpapi.game.model.task.Completed;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class TaskRequest {
    private String name;
    private String description;
    private String type;
    private String location;
    private Float reward;
}
