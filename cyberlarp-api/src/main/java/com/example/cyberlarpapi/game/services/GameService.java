package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.model.Game;
import com.example.cyberlarpapi.game.model.room.Room;
import com.example.cyberlarpapi.game.exceptions.GameException.GameServiceException;
import com.example.cyberlarpapi.game.repositories.GameRepository;
import com.example.cyberlarpapi.game.repositories.room.RoomRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final RoomRepository roomRepository;

    private final GameRepository gameRepository;

    public GameService(RoomRepository roomRepository, GameRepository gameRepository) {
        this.roomRepository = roomRepository;
        this.gameRepository = gameRepository;
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

    // ================== Igor ===================

    public List<Game> getAll() {
        return StreamUtils.createStreamFromIterator(gameRepository.findAll().iterator()).toList();
    }

    public Game getById(int id) throws GameServiceException {
        return gameRepository.findById(id).orElseThrow(() -> new GameServiceException("Game not found"));
    }

    public void deleteById(int id) {
        gameRepository.deleteById(id);
    }

    public Game create(Game game) {
        return gameRepository.save(game);
    }

    public Game update(Game game) {
        return gameRepository.save(game);
    }



}
