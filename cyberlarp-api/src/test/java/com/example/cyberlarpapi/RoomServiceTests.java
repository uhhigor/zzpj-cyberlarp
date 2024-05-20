package com.example.cyberlarpapi;

import com.example.cyberlarpapi.game.exceptions.RoomException.RoomServiceException;
import com.example.cyberlarpapi.game.model.room.Room;
import com.example.cyberlarpapi.game.repositories.room.RoomRepository;
import com.example.cyberlarpapi.game.services.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Optional;

@SpringBootTest
public class RoomServiceTests {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void testCreateRoom() throws RoomServiceException {
        User user = new User();
        Room room = new Room();

        Mockito.when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(roomRepository.save(Mockito.any(Room.class))).thenReturn(room);

        Room result = roomService.createRoom(user);
        Assert.isTrue(result == room, "Objects are not equal");
    }

    @Test
    public void testGetRoomById() throws RoomServiceException {
        Integer id = 1;
        User user = new User();
        Room room = new Room(user);

        Mockito.when(roomRepository.findById(id)).thenReturn(Optional.of(room));

        Room result = roomService.getRoomById(id);
        Assert.isTrue(result == room, "Objects are not equal");
    }

    @Test
    public void testUpdateRoomById() throws RoomServiceException {
        Integer id = 1;
        User user = new User();
        Room room = new Room(user);
        Room updatedRoom = new Room(user);

        Mockito.when(roomRepository.findById(id)).thenReturn(Optional.of(room));
        Mockito.when(roomRepository.save(Mockito.any(Room.class))).thenReturn(updatedRoom);

        Room result = roomService.updateRoomById(id, updatedRoom);
        Assert.isTrue(result == updatedRoom, "Objects are not equal");
    }

    @Test
    public void testDeleteRoomById() {
        Integer id = 1;

        Mockito.doNothing().when(roomRepository).deleteById(id);

        roomService.deleteRoomById(id);

        Mockito.verify(roomRepository, Mockito.times(1)).deleteById(id);
    }
}
