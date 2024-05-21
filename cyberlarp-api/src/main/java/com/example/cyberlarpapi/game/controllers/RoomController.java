package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.model.user.User;
import com.example.cyberlarpapi.game.exceptions.RoomException.RoomServiceException;
import com.example.cyberlarpapi.game.model.room.Room;
import com.example.cyberlarpapi.game.services.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Object> createRoom(@RequestBody User user) {
        try {
            return ResponseEntity.ok(roomService.createRoom(user));
        } catch (RoomServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRoomById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(roomService.getRoomById(id));
        } catch (RoomServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> updateRoomById(@PathVariable Integer id, @RequestBody Room room) {
        try {
            return ResponseEntity.ok(roomService.updateRoomById(id, room));
        } catch (RoomServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.ok().build();
    }
}
