package com.example.cyberlarpapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameTests {
    @Autowired
    private MockMvc mockMvc;

    // Scenario 1: Create a new game
    // 1. Create new user
    // 2. Create new game with the user as the game master
    @Test
    public void createGame() {
        String userRequest = """
                {
                "username": "user1"
                }
                """;

        try {
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String gameRequest = """
                {
                "name": "Game 1",
                "description": "This is an example game",
                "gameMasterUserId": 1
                }
                """;

        try {
            mockMvc.perform(post("/game")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(gameRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Game created successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 2: Get game by id
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Get the game by id
    @Test
    public void getGameById() {
        String userRequest = """
                {
                "username": "user1"
                }
                """;

        try {
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String gameRequest = """
                {
                "name": "Game 1",
                "description": "This is an example game",
                "gameMasterUserId": 1
                }
                """;

        try {
            mockMvc.perform(post("/game")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(gameRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Game created successfully"))
                    .andExpect(jsonPath("$.game.id").exists())
                    .andExpect(jsonPath("$.game.name").value("Game 1"))
                    .andExpect(jsonPath("$.game.description").value("This is an example game"))
                    .andExpect(jsonPath("$.game.gameMasterId").value(1));


        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/game/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").doesNotExist())
                    .andExpect(jsonPath("$.game.id").exists())
                    .andExpect(jsonPath("$.game.name").value("Game 1"))
                    .andExpect(jsonPath("$.game.description").value("This is an example game"))
                    .andExpect(jsonPath("$.game.gameMasterId").value(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


}
