package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.game.model.user.User;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
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

    private final CharacterService characterService;

    public GameService(RoomRepository roomRepository, GameRepository gameRepository, CharacterService characterService) {
        this.roomRepository = roomRepository;
        this.gameRepository = gameRepository;
        this.characterService = characterService;
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

    public Game getById(int id) throws GameNotFoundException {
        return gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException("Game " + id + " not found"));
    }

    public void deleteById(int id) throws GameServiceException {
        if(!gameRepository.existsById(id))
            throw new GameServiceException("Game " + id + " not found");
        gameRepository.deleteById(id);
    }

    public Game save(Game game) {

        return gameRepository.save(game);
    }
}
