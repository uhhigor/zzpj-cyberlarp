package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.task.Completed;
import com.example.cyberlarpapi.game.model.task.Task;
import com.example.cyberlarpapi.game.repositories.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CharacterService characterService;

    public Task save(Task task) {
        taskRepository.save(task);
        return task;
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public Task getById(Integer id) throws TaskNotFoundException {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
    }

    public void setTaskStatus(Task task, Completed status) {
        task.setStatus(status);
        taskRepository.save(task);
    }

    public void completeTask(Task task, Character character) {
        task.setStatus(Completed.SUCCESS);
        taskRepository.save(task);

        character.setBalance(character.getBalance() + task.getReward());
        characterService.save(character);
    }

    public void assignTask(Task task, Character character) {
        character.getTasks().add(task);
        task.setAssignedCharacter(character);
        taskRepository.save(task);
        characterService.save(character);
    }

    public void deassignTask(Task task, Character character) {
        character.getTasks().remove(task);
        task.setAssignedCharacter(null);
        taskRepository.save(task);
        characterService.save(character);
    }
}
