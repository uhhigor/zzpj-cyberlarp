package com.example.cyberlarpapi.game.controllers;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.game.Game;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    private ResponseEntity<TaskResponse> checkCharacter(@AuthenticationPrincipal UserDetails userDetails, TaskRequest taskRequest) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not logged in", null));
        }
        if (taskRequest.getCharacterId() == null) {
            return ResponseEntity.badRequest().body(new TaskResponse("Character ID is required", null));
        }
        Character character;
        try {
            character = characterService.getById(taskRequest.getCharacterId());
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if (!Objects.equals(character.getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your character", null));
        }

        if (character.getCharacterClass() != CharacterClass.FIXER) {
            return ResponseEntity.badRequest().body(new TaskResponse("Only Fixers can create or update tasks", null));
        }

        return null;
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

        Character character;
        try {
            character = characterService.getById(taskRequest.getCharacterId());
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if(!game.getCharacters().contains(character)) {
            return ResponseEntity.badRequest().body(new TaskResponse("Character not in game", null));
        }

        if(!character.getUser().equals(user)) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your character", null));
        }

        if(character.getCharacterClass() != CharacterClass.FIXER) {
            return ResponseEntity.badRequest().body(new TaskResponse("Only Fixers can create tasks", null));
        }

        try {
            Task task = new Task.TaskBuilder()
                    .withCharacter(character)
                    .withName(taskRequest.getName())
                    .withDescription(taskRequest.getDescription())
                    .withStatus(taskRequest.getStatus())
                    .withType(taskRequest.getType())
                    .withLocation(taskRequest.getLocation())
                    .withReward(taskRequest.getReward())
                    .withDeadline(taskRequest.getDeadline())
                    .withCompletionDate(taskRequest.getCompletionDate())
                    .withCompletionTime(taskRequest.getCompletionTime())
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
        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
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
        if (taskRequest.getDeadline() != null) {
            task.setDeadline(taskRequest.getDeadline());
        }
        if (taskRequest.getCompletionDate() != null) {
            task.setCompletionDate(taskRequest.getCompletionDate());
        }
        if (taskRequest.getCompletionTime() != null) {
            task.setCompletionTime(taskRequest.getCompletionTime());
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

    @Operation(summary = "Set task as complete", description = "Set task as complete in the game by providing id and reward")
    @PostMapping("/complete/{id}")
    public ResponseEntity<TaskResponse> completeTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id, @RequestParam Float reward) throws TaskNotFoundException {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        taskService.completeTask(id, reward);

        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Set task as incomplete", description = "Set task as incomplete in the game by providing id")
    @PostMapping("/incomplete/{id}")
    public ResponseEntity<TaskResponse> incompleteTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) throws TaskNotFoundException {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        taskService.incompleteTask(id);

        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Get task by id", description = "Get task by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<TaskResponse> getTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) throws TaskNotFoundException {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Assign task to character", description = "Assign task to character in the game by providing task id and character id")
    @PostMapping("/assign/{id}")
    public ResponseEntity<TaskResponse> assignTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id, @RequestParam Integer characterId) throws TaskNotFoundException, CharacterNotFoundException {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        taskService.assignTask(id, characterId);

        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Unassign task from character", description = "Unassign task from character in the game by providing task id")
    @PostMapping("/unassign/{id}")
    public ResponseEntity<TaskResponse> unassignTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) throws TaskNotFoundException {
        Task task;

        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        taskService.unassignTask(id);

        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Get all tasks assigned to character", description = "Get all tasks assigned to character in the game by providing character id")
    @GetMapping("/all/{characterId}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Integer characterId) throws CharacterNotFoundException, TaskNotFoundException {
        return ResponseEntity.ok(taskService.getAllTasksForCharacter(characterId));
    }
}
