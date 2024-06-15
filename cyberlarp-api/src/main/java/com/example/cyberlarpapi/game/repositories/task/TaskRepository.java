package com.example.cyberlarpapi.game.repositories.task;

import com.example.cyberlarpapi.game.model.task.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Integer>{
}
