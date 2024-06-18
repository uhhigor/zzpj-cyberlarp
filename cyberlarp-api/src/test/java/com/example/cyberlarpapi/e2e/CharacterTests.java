package com.example.cyberlarpapi.e2e;

import com.example.cyberlarpapi.e2e.secutity.CustomSecurityPostProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/").with(CustomSecurityPostProcessor.applySecurityForUser1()))
                .alwaysDo(print())
                .build();
        createUser("user1");
    }

    private Integer createUser(String username) throws Exception {
        String userRequest = String.format("""
                {
                "username": "%s"
                }
                """, username);

        String response = mockMvc.perform(get("/users/user")
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

    private void createCharacter(Integer gameId, String characterName) throws Exception {
        String characterRequest = String.format("""
           {
           "name": "%s",
           "description": "This is an example character",
           "characterClass": "PUNK",
           "faction": "GOVERNMENT",
           "style": "KITSCH",
           "strength": 10,
           "agility": 2,
           "presence": 2,
           "toughness": 2,
           "knowledge": 4,
           "maxHp": 10,
           "currentHp": 10,
           "balance": 1000
           }
            """, characterName);

        mockMvc.perform(post("/game/" + gameId + "/character/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk());
    }

    private void assignCharacterToUser(Integer gameId, Integer userId, Integer characterId) throws Exception {
        mockMvc.perform(post("/game/" + gameId + "/character/"+ characterId+"/assignUser/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createCharacterForGame() {
        try {
            Integer userId = createUser("user1");
            createGame(userId, "Game 1");
            createCharacter( 1, "Character 1");
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
           "description": "This is an example character",
           "characterClass": "PUNK",
           "faction": "GOVERNMENT",
           "style": "KITSCH",
           "strength": 10,
           "agility": 2,
           "presence": 2,
           "toughness": 2,
           "knowledge": 4,
           "maxHp": 10,
           "currentHp": 10,
           "balance": 1000
           }
            """;

            mockMvc.perform(post("/game/1/character/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Name is required"));

            characterRequest = """
                    {
                    "name": "Character 1",
                    "description": "This is an example character",
                    "characterClass": "PUNK",
                    "faction": "GOVERNMENT",
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

            mockMvc.perform(post("/game/1/character/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Attributes must sum up to 20"));

            characterRequest = """
                    {
                    "name": "Character 1",
                    "description": "This is an example character",
                    "characterClass": "INVALID",
                    "faction": "GOVERNMENT",
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

            mockMvc.perform(post("/game/1/character/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid character class"));

            characterRequest = """
                    {
                    "name": "Character 1",
                    "description": "This is an example character",
                    "characterClass": "PUNK",
                    "faction": "GOVERNMENT",
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

            mockMvc.perform(post("/game/1/character/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid style"));

            characterRequest = """
                    {
                    "name": "Character 1",
                    "description": "This is an example character",
                    "characterClass": "PUNK",
                    "faction": "INVALID",
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

            mockMvc.perform(post("/game/1/character/")
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
            createCharacter( 1, "Character 1");

            String characterRequest = """
                    {
                    "name": "Changed 1",
                    "description": "This is an example changed",
                    "characterClass": "PUNK",
                    "faction": "GOVERNMENT",
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

            mockMvc.perform(post("/game/1/character/4")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 4 updated successfully"))
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
            createCharacter( 1, "Character 1");

            mockMvc.perform(delete("/game/1/character/4"))
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
            createCharacter( 1, "Character 1");
            assignCharacterToUser(1, 1, 4);

            mockMvc.perform(post("/game/1/action/roll/strength")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isNumber());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    @WithMockUser(username = "user1")
    public void rollAttributeForCharacterWithNoCharacterAssignedToUser() {
        try {
            createGame(1, "Game 1");
            createCharacter( 1, "Character 1");

            mockMvc.perform(post("/game/1/action/roll/strength")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Character not found"));
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
            createCharacter( 1, "Character 1");
            assignCharacterToUser(1, 1, 4);
            mockMvc.perform(post("/game/1/action/roll/invalid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid attribute value: invalid"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

}
