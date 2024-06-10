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
    private final GameService gameService;

    private Task setsTask(TaskRequest taskRequest, Integer id) throws GameNotFoundException, CharacterNotFoundException, TaskNotFoundException {
        Task task;
        if (!taskRepository.existsById(id)) {
            task = new Task();
        }
        else {
            task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
        }
        if (characterService.getById(taskRequest.getCharacterId()) == null) {
            throw new CharacterNotFoundException("Character not found");
        }
        if (gameService.getById(taskRequest.getGameId()) == null) {
            throw new GameNotFoundException("Game not found");
        }
        Character character = characterService.getById(taskRequest.getCharacterId());
        Game game = gameService.getById(taskRequest.getGameId());
        task.setCharacter(character);
        task.setGame(game);
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setType(taskRequest.getType());
        task.setLocation(taskRequest.getLocation());
        task.setReward(taskRequest.getReward());
        task.setDeadline(taskRequest.getDeadline());
        task.setCompletionDate(taskRequest.getCompletionDate());
        task.setCompletionTime(taskRequest.getCompletionTime());

        return task;
    }

    public Task createTask(TaskRequest taskRequest) throws GameNotFoundException, CharacterNotFoundException, TaskNotFoundException {
        Task task = setsTask(taskRequest, null);
        taskRepository.save(task);
        return task;
    }

    public Task updateTask(TaskRequest taskRequest, Integer id) throws TaskNotFoundException, CharacterNotFoundException, GameNotFoundException {
        Task task = setsTask(taskRequest, id);
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(Integer id) throws TaskNotFoundException {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task " + id + " not found");
        }
        taskRepository.deleteById(id);
    }

    public Task getById(Integer id) throws TaskNotFoundException {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task " + id + " not found"));
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

    public List<Task> getAllTasksForGame(Integer gameId) throws GameNotFoundException, TaskNotFoundException {
        Game game = gameService.getById(gameId);
        try {
            List<Task> tasks = (List<Task>) taskRepository.findAll();
            for (Task task : tasks) {
                if (task.getGame().equals(game)) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (Exception e) {
            throw new TaskNotFoundException("Tasks not found");

        }
    }

}
