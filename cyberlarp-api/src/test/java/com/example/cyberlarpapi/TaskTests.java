package com.example.cyberlarpapi;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.GameException.GameNotFoundException;
import com.example.cyberlarpapi.game.exceptions.TaskException.TaskNotFoundException;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.GameService;
import com.example.cyberlarpapi.game.services.TaskService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskTests {

    @Autowired
    private MockMvc mockMvc;

    //Create a task
//    @Test
//    void createTask() {
//        String userRequest = """
//                {
//                    "username": "user1"
//                }
//                """;
//
//        try {
//            mockMvc.perform(post("/users")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(userRequest))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.id").exists());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception thrown", e);
//        }
//
//        String gameRequest = """
//                {
//                "name": "Game 1",
//                "description": "This is an example game",
//                "gameMasterUserId": 1
//                }
//                """;
//
//        try {
//            mockMvc.perform(post("/game")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(gameRequest))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("Game created successfully"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception thrown", e);
//        }
//
//        String factionRequest = """
//                {
//                "name": "Faction 1",
//                "description": "This is an example faction"
//                }
//                """;
//
//        try {
//            mockMvc.perform(post("/factions")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(factionRequest))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.id").exists());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception thrown", e);
//        }
//
//
//        String characterRequest = """
//                {
//                "name": "Character 1",
//                "description": "This is an example character",
//                "characterClass": "FIXER",
//                "factionId": 1,
//                "style": "KITSCH",
//                "strength": 10,
//                "agility": 2,
//                "presence": 2,
//                "toughness": 2,
//                "knowledge": 4,
//                "maxHp": 10,
//                "currentHp": 10,
//                "balance": 10
//                }
//                """;
//
//        try {
//            mockMvc.perform(post("/characters/game/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(characterRequest))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("Character 1 added to game 1"))
//                    .andExpect(jsonPath("$.character.id").exists())
//                    .andExpect(jsonPath("$.character.name").value("Character 1"))
//                    .andExpect(jsonPath("$.character.description").value("This is an example character"))
//                    .andExpect(jsonPath("$.character.characterClass").value("FIXER"))
//                    .andExpect(jsonPath("$.character.style").value("KITSCH"))
//                    .andExpect(jsonPath("$.character.strength").value(10))
//                    .andExpect(jsonPath("$.character.agility").value(2))
//                    .andExpect(jsonPath("$.character.presence").value(2))
//                    .andExpect(jsonPath("$.character.toughness").value(2))
//                    .andExpect(jsonPath("$.character.knowledge").value(4))
//                    .andExpect(jsonPath("$.character.maxHp").value(10))
//                    .andExpect(jsonPath("$.character.currentHp").value(10))
//                    .andExpect(jsonPath("$.character.balance").value(10));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception thrown", e);
//        }
//
//        String taskRequest = """
//                {
//                "characterId": 1,
//                "gameId": 1,
//                "name": "Task 1",
//                "description": "This is an example task",
//                "status": "IN_PROGRESS",
//                "type": "DELIVERY",
//                "location": "Location 1",
//                "reward": 100.0,
//                "deadline": "2022-12-31",
//                "completionDate": "2022-12-31",
//                "completionTime": "12:00"
//                }
//                """;
//
//        try {
//            mockMvc.perform(post("/task/create")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(taskRequest))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("Task created successfully"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception thrown", e);
//        }
//
//        String taskRequest2 = """
//                {
//                "characterId": 1,
//                "gameId": 1,
//                "name": "Task 2",
//                "description": "This is an example task",
//                "status": "IN_PROGRESS",
//                "type": "DELIVERY",
//                "location": "Location 1",
//                "reward": 100.0,
//                "deadline": "2022-12-31",
//                "completionDate": "2022-12-31",
//                "completionTime": "12:00"
//                }
//                """;
//
//        try {
//            mockMvc.perform(post("/task/create")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(taskRequest2))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("Task created successfully"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception thrown", e);
//        }
//    }

}
