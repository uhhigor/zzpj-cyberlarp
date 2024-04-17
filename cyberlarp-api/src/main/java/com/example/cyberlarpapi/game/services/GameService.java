package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.UserRepository;
import com.example.cyberlarpapi.game.data.room.Room;
import com.example.cyberlarpapi.game.repositories.room.RoomRepository;

public class GameService {
    private final RoomRepository roomRepository;

    public GameService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(User user) {
        for (Room room : this.roomRepository.findAll()) {
            if (room.getOwner().equals(user)) {
                return null;
            }
        }
        Room room = new Room(user);
        this.roomRepository.save(room);
        return room;
    }

    public boolean inviteUserToRoom(Integer roomId, User user) {
        for (Room room : this.roomRepository.findAll()) {
            if (room.getId().equals(roomId)) {
                if (room.findUser(user.getId()) == null) {
                    return room.addUser(user);
                }
            }
        }
        return false;
    }

    public boolean kickUserFromRoom(Integer roomId, User user) {
        for (Room room : this.roomRepository.findAll()) {
            if (room.getId().equals(roomId)) {
                if (room.findUser(user.getId()) != null) {
                    return room.removeUser(user);
                }
            }
        }
        return false;
    }

    public boolean makeUserOwnerOfRoom(Integer roomId, User user) {
        for (Room room : this.roomRepository.findAll()) {
            if (room.getId().equals(roomId)) {
                if (room.getOwner() != user) {
                    room.setOwner(user);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startGame() {
        return false;
    }

}
