package com.example.cyberlarpapi.game.model.task.DTO;

import com.example.cyberlarpapi.game.model.task.Completed;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class TaskRequest {
    private Integer characterId;
    private String name;
    private String description;
    private Completed status;
    private String type;
    private String location;
    private Float reward;
    private LocalDate deadline;
    private LocalDate completionDate;
    private LocalTime completionTime;
}
