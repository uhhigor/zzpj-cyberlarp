package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.task.Completed;
import com.example.cyberlarpapi.game.model.task.DTO.TaskRequest;
import com.example.cyberlarpapi.game.repositories.task.TaskRepository;
import com.example.cyberlarpapi.game.model.task.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Task task;
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task " + id + " not found");
        }
        else {
             task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
        }
        return task;
    }

    public void completeTask(Integer id, Float reward) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
        task.setStatus(Completed.SUCCESS);
        task.setReward(reward);
        taskRepository.save(task);

        Character character = task.getCharacter();
        character.setBalance(character.getBalance() + reward);
        characterService.save(character);
    }

    public void incompleteTask(Integer id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
        task.setStatus(Completed.FAILURE);
        taskRepository.save(task);
    }

    public void assignTask(Integer id, Integer characterId) throws TaskNotFoundException, CharacterNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
        Character character = characterService.getById(characterId);
        task.setCharacter(character);
        taskRepository.save(task);
    }

    public void unassignTask(Integer id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
        task.setCharacter(null);
        taskRepository.save(task);
    }

    public List<Task> getAllTasksForCharacter(Integer characterId) throws CharacterNotFoundException, TaskNotFoundException {
        Character character = characterService.getById(characterId);
        try {
            List<Task> tasks = (List<Task>) taskRepository.findAll();
            for (Task task : tasks) {
                if (task.getCharacter().equals(character)) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (Exception e) {
            throw new TaskNotFoundException("Tasks not found");
        }
    }

}
