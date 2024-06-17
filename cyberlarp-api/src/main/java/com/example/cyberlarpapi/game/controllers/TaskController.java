package com.example.cyberlarpapi.game.controllers;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.task.DTO.TaskRequest;
import com.example.cyberlarpapi.game.model.task.Task;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.TaskService;
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
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final CharacterService characterService;

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

    @Operation(summary = "Create a new task", description = "Create a new task in the game, providing name, description, reward and character id")
    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TaskRequest taskRequest) throws CharacterNotFoundException, GameNotFoundException{
        ResponseEntity<TaskResponse> response = checkCharacter(userDetails, taskRequest);

        if (response != null) {
            return response;
        }
        Task task;

        try {
           task = taskService.createTask(taskRequest);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(new TaskResponse(null, task));
    }


    @Operation(summary = "Update a task", description = "Update a task in the game, providing name, description, reward and character id")
    @PostMapping("/update/{id}")
    public ResponseEntity<TaskResponse> updateTask(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TaskRequest taskRequest, @PathVariable Integer id) throws CharacterNotFoundException, GameNotFoundException {
        ResponseEntity<TaskResponse> response = checkCharacter(userDetails, taskRequest);

        if (response != null) {
            return response;
        }

        Task task;
        try {
            task = taskService.updateTask(taskRequest, id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new TaskResponse(null, task));
    }

    @Operation(summary = "Delete a task", description = "Delete a task in the game by providing id")
    @PostMapping("/delete/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) throws TaskNotFoundException {
        Task task;
        try {
            task = taskService.getById(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        taskService.deleteTask(id);

        return ResponseEntity.ok(new TaskResponse(null, null));
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
