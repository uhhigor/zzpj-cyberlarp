package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.UserRepository;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.model.character.CharacterClass;
import com.example.cyberlarpapi.game.model.player.Player;
import com.example.cyberlarpapi.game.model.task.DTO.TaskRequest;
import com.example.cyberlarpapi.game.model.task.Task;
import com.example.cyberlarpapi.game.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest taskRequest) throws CharacterNotFoundException, GameNotFoundException, TaskNotFoundException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                Task task = taskService.createTask(taskRequest);
                return ResponseEntity.ok(task);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Task> updateTask(@RequestBody TaskRequest taskRequest, Integer id) throws CharacterNotFoundException, GameNotFoundException, TaskNotFoundException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                Task task = taskService.updateTask(taskRequest, id);
                return ResponseEntity.ok(task);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/delete")
    public void deleteTask(Integer id, @RequestBody TaskRequest taskRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                try {
                    taskService.deleteTask(id);
                } catch (TaskNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @PostMapping("/complete")
    public void completeTask(Integer id, @RequestBody TaskRequest taskRequest, Float reward) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                try {
                    taskService.completeTask(id, reward);
                } catch (TaskNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/incomplete")
    public void incompleteTask(Integer id, @RequestBody TaskRequest taskRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                try {
                    taskService.incompleteTask(id);
                } catch (TaskNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @PostMapping("/assign")
    public void assignTask(Integer id, Integer characterId, @RequestBody TaskRequest taskRequest) throws CharacterNotFoundException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                try {
                    taskService.assignTask(id, characterId);
                } catch (TaskNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/unassign")
    public void unassignTask(Integer id, @RequestBody TaskRequest taskRequest)  {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Player> players = user.getPlayers();
        for (Player player : players) {
            if (player.getGame().getId().equals(taskRequest.getGameId()) && player.getCharacter().getCharacterClass().equals(CharacterClass.FIXER)) {
                try {
                    taskService.unassignTask(id);
                } catch (TaskNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Task> getTask(Integer id) {
        try {
            Task task = taskService.getById(id);
            return ResponseEntity.ok(task);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getall/character")
    public ResponseEntity<List<Task>> getAllTasksForCharacter(Integer characterId) throws CharacterNotFoundException {
        try {
            List<Task> tasks = taskService.getAllTasksForCharacter(characterId);
            return ResponseEntity.ok(tasks);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getall/game")
    public ResponseEntity<List<Task>> getAllTasksForGame(Integer gameId) throws GameNotFoundException {
        try {
            List<Task> tasks = taskService.getAllTasksForGame(gameId);
            return ResponseEntity.ok(tasks);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
