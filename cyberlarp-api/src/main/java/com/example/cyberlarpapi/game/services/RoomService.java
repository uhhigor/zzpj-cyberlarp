package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.exceptions.RoomException.RoomServiceException;
import com.example.cyberlarpapi.game.model.room.Room;
import com.example.cyberlarpapi.game.repositories.room.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    public Room createRoom(User user) throws RoomServiceException {
        try {
            for (Room room : this.roomRepository.findAll()) {
                if (room.getOwner().equals(user)) {
                    return null;
                }
            }
            Room room = new Room(user);
            this.roomRepository.save(room);
            return room;
        } catch (Exception e) {
            throw new RoomServiceException("Error while creating room", e);
        }
    }

    public Room getRoomById(Integer id) throws RoomServiceException {
        return this.roomRepository.findById(id).orElseThrow(() -> new RoomServiceException("Room not found"));
    }

    public Room updateRoomById(Integer id, Room room) throws RoomServiceException {
        try {
            Room oldRoom = getRoomById(id);
            oldRoom.setOwner(room.getOwner());
            oldRoom.setUsers(room.getUsers());
            return roomRepository.save(oldRoom);
        } catch (RoomServiceException e) {
            throw new RoomServiceException("Error while updating room", e);
        }
    }

    public void deleteRoomById(Integer id) {
        roomRepository.deleteById(id);
    }
}
