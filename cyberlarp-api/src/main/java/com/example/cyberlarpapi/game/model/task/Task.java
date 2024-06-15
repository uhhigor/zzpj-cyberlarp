package com.example.cyberlarpapi.game.model.task;

import com.example.cyberlarpapi.game.exceptions.TaskException.TaskException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.game.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @Setter
    private Character character;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private Completed status;

    @Setter
    private String type;

    @Setter
    private String location;

    @Setter
    private Float reward;

    @Setter
    private LocalDate deadline;

    @Setter
    private LocalDate completionDate;

    @Setter
    private LocalTime completionTime;


    public Task() {
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }


    public static class TaskBuilder {
        private final Task task = new Task();

        public TaskBuilder withCharacter(Character character) {
            task.setCharacter(character);
            return this;
        }

        public TaskBuilder withName(String name) {
            task.setName(name);
            return this;
        }

        public TaskBuilder withDescription(String description) {
            task.setDescription(description);
            return this;
        }

        public TaskBuilder withStatus(Completed status) {
            task.setStatus(status);
            return this;
        }

        public TaskBuilder withType(String type) {
            task.setType(type);
            return this;
        }

        public TaskBuilder withLocation(String location) {
            task.setLocation(location);
            return this;
        }

        public TaskBuilder withReward(Float reward) {
            task.setReward(reward);
            return this;
        }

        public TaskBuilder withDeadline(LocalDate deadline) {
            task.setDeadline(deadline);
            return this;
        }

        public TaskBuilder withCompletionDate(LocalDate completionDate) {
            task.setCompletionDate(completionDate);
            return this;
        }

        public TaskBuilder withCompletionTime(LocalTime completionTime) {
            task.setCompletionTime(completionTime);
            return this;
        }

        public Task build() throws TaskException {
            if (task.getCharacter() == null) {
                throw new TaskException("Character must be set");
            }
            return task;
        }
    }
}
