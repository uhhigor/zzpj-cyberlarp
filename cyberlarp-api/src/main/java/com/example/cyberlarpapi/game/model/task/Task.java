package com.example.cyberlarpapi.game.model.task;

import com.example.cyberlarpapi.game.exceptions.TaskException.TaskException;
import com.example.cyberlarpapi.game.model.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private TaskStatus status = TaskStatus.IN_PROGRESS;

    @Setter
    private String type;

    @Setter
    private String location;

    @Setter
    private Float reward;

    @ManyToOne
    @Setter
    private Character assignedCharacter;

    public Task() {
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }


    public static class TaskBuilder {
        private final Task task = new Task();

        public TaskBuilder withName(String name) {
            task.setName(name);
            return this;
        }

        public TaskBuilder withDescription(String description) {
            task.setDescription(description);
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

        public Task build() throws TaskException {
            return task;
        }
    }
}
