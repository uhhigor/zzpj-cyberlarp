package com.example.cyberlarpapi.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class CharacterTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        createUser("user1");
    }

    private Integer createUser(String username) throws Exception {
        String userRequest = String.format("""
                {
                "username": "%s"
                }
                """, username);

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asInt();
    }

    private void createGame(Integer userId, String gameName) throws Exception {
        String gameRequest = String.format("""
                {
                "name": "%s",
                "description": "This is an example game",
                "gameMasterUserId": %d
                }
                """, gameName, userId);

        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gameRequest))
                .andExpect(status().isOk());
    }

    private void createCharacter(Integer userId, Integer gameId, String characterName) throws Exception {
        String characterRequest = String.format("""
                {
                "userId": %d,
                "gameId": %d,
                "name": "%s",
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
                """, userId, gameId, characterName);

        mockMvc.perform(post("/characters/game/" + gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void createCharacterForGame() {
        try {
            Integer userId = createUser("user1");
            createGame(userId, "Game 1");
            createCharacter(userId, 1, "Character 1");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void createCharacterForGameWithInvalidData() {
        try {
            Integer userId = createUser("user1");
            createGame(userId, "Game 1");

            String characterRequest = """
                    {
                    "userId": %d,
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
                    """.formatted(userId);

            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Name is required"));

            characterRequest = """
                    {
                    "userId": %d,
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
                    """.formatted(userId);

            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Attributes must sum up to 20"));

            characterRequest = """
                    {
                    "userId": %d,
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
                    """.formatted(userId);

            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid character class"));

            characterRequest = """
                    {
                    "userId": %d,
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
                    """.formatted(userId);

            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid style"));

            characterRequest = """
                    {
                    "userId": %d,
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
                    """.formatted(userId);

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

    @Test
    public void updateCharacterForGame() {
        try {
            Integer userId = createUser("user1");
            createGame(userId, "Game 1");
            createCharacter(userId, 1, "Character 1");

            String factionRequest = """
                    {
                    "name": "Faction 1",
                    "description": "This is an example faction"
                    }
                    """;

            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().is(201));

            String characterRequest = """
                    {
                    "userId": %d,
                    "gameId": 1,
                    "name": "Changed 1",
                    "description": "This is an example changed",
                    "characterClass": "PUNK",
                    "factionId": 1,
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
                    """.formatted(userId);

            mockMvc.perform(post("/characters/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 1 updated successfully"))
                    .andExpect(jsonPath("$.character.name").value("Changed 1"))
                    .andExpect(jsonPath("$.character.description").value("This is an example changed"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void deleteCharacterForGame() {
        try {
            Integer userId = createUser("user1");
            createGame(userId, "Game 1");
            createCharacter(userId, 1, "Character 1");

            mockMvc.perform(delete("/characters/1/1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    @WithMockUser(username = "user1")
    public void rollAttributeForCharacter() {
        try {
            createGame(1, "Game 1");
            createCharacter(1, 1, "Character 1");

            mockMvc.perform(post("/action/roll/strength")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "characterId": 1
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isNumber());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    @WithMockUser(username = "user1")
    public void rollAttributeForCharacterWithInvalidData() {
        try {
            createGame(1, "Game 1");
            createCharacter(1, 1, "Character 1");

            mockMvc.perform(post("/action/roll/strength")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Character ID is required"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    @WithMockUser(username = "user1")
    public void rollAttributeForCharacterWithInvalidAttribute() {
        try {
            createGame(1, "Game 1");
            createCharacter(1, 1, "Character 1");

            mockMvc.perform(post("/action/roll/invalid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "characterId": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid attribute"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

}
