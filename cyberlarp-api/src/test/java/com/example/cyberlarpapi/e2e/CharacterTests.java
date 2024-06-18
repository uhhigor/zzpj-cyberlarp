package com.example.cyberlarpapi.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class CharacterTests {
    @Autowired
    private MockMvc mockMvc;

    // Scenario 1: Create a new character
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new character in the game

    @Test
    public void createCharacterForGame() {
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

        String characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "name": "Character 1",
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": null,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(characterRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 1 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 1"))
                    .andExpect(jsonPath("$.character.description").value("This is an example character"))
                    .andExpect(jsonPath("$.character.characterClass").value("PUNK"))
                    .andExpect(jsonPath("$.character.style").value("KITSCH"))
                    .andExpect(jsonPath("$.character.strength").value(10))
                    .andExpect(jsonPath("$.character.agility").value(2))
                    .andExpect(jsonPath("$.character.presence").value(2))
                    .andExpect(jsonPath("$.character.toughness").value(2))
                    .andExpect(jsonPath("$.character.knowledge").value(4))
                    .andExpect(jsonPath("$.character.maxHp").value(10))
                    .andExpect(jsonPath("$.character.currentHp").value(10))
                    .andExpect(jsonPath("$.character.balance").value(10));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 2: Create a new character with invalid data
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new character in the game with missing data
    // 4. Create new character in the game with invalid attribute values
    // 5. Create new character in the game with invalid character class
    // 6. Create new character in the game with invalid style
    // 7. Create new character in the game with invalid faction

    @Test
    public void createCharacterForGameWithInvalidData() {
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

        String characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": null,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Name is required"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "name": "Character 1",
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": null,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 10,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Attributes must sum up to 20"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "name": "Character 1",
                "description": "This is an example character",
                "characterClass": "INVALID",
                "factionId": null,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid character class"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "name": "Character 1",
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": null,
                "style": "INVALID",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid style"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "name": "Character 1",
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": 99,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid faction"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

    }

    // Scenario 3: Add character to user
    // 1. Create new user
    // 2. Create new game
    // 3. Create new character
    // 4. Add character to user
    // 5. Create new game
    // 6. Create new character
    // 7. Add character to user
    // 8. Check that the user has the characters

    @Test
    public void addCharacterToUser() {
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

        String characterRequest = """
                {
                "userId": 1,
                "gameId": 1,
                "name": "Character 1",
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": null,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 1 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        gameRequest = """
                {
                "name": "Game 2",
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

        characterRequest = """
                {
                "userId": 1,
                "gameId": 2,
                "name": "Character 2",
                "description": "This is an example character",
                "characterClass": "PUNK",
                "factionId": null,
                "style": "KITSCH",
                "strength": 10,
                "agility": 2,
                "presence": 2,
                "toughness": 2,
                "knowledge": 4,
                "maxHp": 10,
                "currentHp": 10,
                "balance": 10
                }
                """;

        try {
            mockMvc.perform(post("/characters/game/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 2 added to game 2"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 2"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/users/characters/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }





    }
}
