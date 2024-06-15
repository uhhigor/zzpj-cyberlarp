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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    @PostMapping("/delete/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) throws TaskNotFoundException {
        Task task = taskService.getById(id);
        if (!Objects.equals(task.getCharacter().getUser().getUsername(), userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new TaskResponse("Not your task", null));
        }

        taskService.deleteTask(id);

        return ResponseEntity.ok(new TaskResponse(null, null));
    }

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

    @GetMapping("/all/{characterId}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Integer characterId) throws CharacterNotFoundException, TaskNotFoundException {
        return ResponseEntity.ok(taskService.getAllTasksForCharacter(characterId));
    }
}
