package com.example.cyberlarpapi.game.controllers;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.game.Game;
import com.example.cyberlarpapi.game.model.task.TaskStatus;
import com.example.cyberlarpapi.game.model.task.DTO.TaskRequest;
import com.example.cyberlarpapi.game.model.task.Task;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.TaskService;
import com.example.cyberlarpapi.game.services.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Operations", description = "Operations related to tasks in specific game")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/{gameId}/task")
public class TaskController {

    private final TaskService taskService;
    private final CharacterService characterService;
    private final GameService gameService;
    private final UserService userService;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TaskResponse(String message, Task task){
    }

    @Operation(summary = "Create new task [FIXER]", description = "Create a new task in the game, providing name, description, reward and character id")
    @PostMapping("/")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer gameId) throws CharacterNotFoundException, UserServiceException {
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        _User user;
        try {
            user = userService.getCurrentUser();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not logged in", null));
        }
        Character sender;
        try {
            sender = game.getUserCharacter(user);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.badRequest().body(new TaskResponse(e.getMessage(), null));
        }

        if(sender.getCharacterClass() != CharacterClass.FIXER) {
            return ResponseEntity.badRequest().body(new TaskResponse("Only Fixers can create tasks", null));
        }

        try {
            Task task = new Task.TaskBuilder()
                    .withName(taskRequest.getName())
                    .withDescription(taskRequest.getDescription())
                    .withType(taskRequest.getType())
                    .withLocation(taskRequest.getLocation())
                    .withReward(taskRequest.getReward())
                    .build();
            task = taskService.save(task);
            game.getTasks().add(task);
            gameService.save(game);
            return ResponseEntity.ok(new TaskResponse(null, task));
        } catch (TaskException e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(summary = "Update task [FIXER]", description = "Update a task in the game, providing name, description, reward and character id")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer gameId, @PathVariable Integer id) {
        Task task;
        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if (taskRequest.getName() != null) {
            task.setName(taskRequest.getName());
        }
        if (taskRequest.getDescription() != null) {
            task.setDescription(taskRequest.getDescription());
        }
        if (taskRequest.getType() != null) {
            task.setType(taskRequest.getType());
        }
        if (taskRequest.getLocation() != null) {
            task.setLocation(taskRequest.getLocation());
        }
        if (taskRequest.getReward() != null) {
            task.setReward(taskRequest.getReward());
        }

        task = taskService.save(task);
        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Delete task [FIXER/GM]", description = "Delete a task in the game by providing id")
    @PostMapping("/delete/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable String gameId, @PathVariable Integer id) {
        Task task;
        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        Game game;
        try {
            game = gameService.getById(Integer.parseInt(gameId));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        game.getTasks().remove(task);
        gameService.save(game);
        taskService.delete(task);
        return ResponseEntity.ok(new TaskResponse("Task deleted successfully", null));
    }

    @Operation(summary = "Set task status [FIXER]", description = "Set task status in the game by providing id and status")
    @PostMapping("/{id}/status/{status}")
    public ResponseEntity<TaskResponse> setTaskStatus(@PathVariable Integer id, @PathVariable String status, @PathVariable String gameId) {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        TaskStatus taskStatusStatus;

        try {
            taskStatusStatus = TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new TaskResponse("Invalid status", null));
        }

        if(taskStatusStatus == TaskStatus.SUCCESS) {
            taskService.completeTask(task, task.getAssignedCharacter());
        } else {
            taskService.setTaskStatus(task, taskStatusStatus);
        }

        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Get task by id", description = "Get task by id")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Integer id, @PathVariable String gameId) {
        Task task;
        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Assign task to character [FIXER]", description = "Assign task to character in the game by providing task id and character id")
    @PostMapping("/{id}/assignCharacter/{characterId}")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Integer id, @PathVariable Integer characterId, @PathVariable String gameId) throws TaskNotFoundException, CharacterNotFoundException {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        Character character;
        try {
            character = characterService.getById(characterId);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        taskService.assignTask(task, character);
        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Deassign task [FIXER]", description = "Deassign task in the game by providing task id")
    @PostMapping("/{id}/deassignCharacter")
    public ResponseEntity<TaskResponse> deassignTask(@PathVariable Integer id, @PathVariable String gameId) {
        Task task;
        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        taskService.deassignTask(task, task.getAssignedCharacter());
        return ResponseEntity.ok(new TaskResponse(null, task));
    }


    @Operation(summary = "Get all tasks assigned to character", description = "Get all tasks assigned to your character in the game")
    @GetMapping("/")
    public ResponseEntity<List<Task>> getCharacterTasks(@PathVariable Integer gameId) {
        _User user;
        try {
            user = userService.getCurrentUser();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().build();
        }
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        Character character;
        try {
            character = game.getUserCharacter(user);
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(character.getTasks());
    }

    @Operation(summary = "Get all tasks in the game", description = "Get all tasks in the game")
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Integer gameId) {
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(game.getTasks());
    }
}
