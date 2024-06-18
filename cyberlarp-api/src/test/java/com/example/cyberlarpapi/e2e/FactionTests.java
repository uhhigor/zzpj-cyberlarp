package com.example.cyberlarpapi.e2e;

import com.example.cyberlarpapi.e2e.secutity.CustomSecurityPostProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FactionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;


    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/").with(CustomSecurityPostProcessor.applySecurity()))
                .build();
        Integer user1Id = createUser("user1");
        createGame(user1Id, "game1");
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

    @Test
    void createFaction() throws Exception {
        Integer user2Id = createUser("user2");
        createGame(user2Id, "game2");

        String factionRequest = String.format("""
                {
                "name": "faction1",
                "description": "This is an example faction",
                "gameId": 1
                }
                """);

        mockMvc.perform(post("/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(factionRequest))
                .andExpect(status().is(201));

        String characterRequest = """
                    {
                    "userId": 1,
                    "gameId": 1,
                    "name": "Character 1",
                    "description": "This is an example description",
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
                    """;

        mockMvc.perform(post("/characters/game/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Character 1 added to game 1"));

        mockMvc.perform(get("/factions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("faction1"))
                .andExpect(jsonPath("$.description").value("This is an example faction"));
    }

    @Test
    public void updateFaction() throws Exception {
        Integer user2Id = createUser("user2");
        createGame(user2Id, "game2");
        String factionRequest = String.format("""
                {
                "name": "faction1",
                "description": "This is an example faction",
                "gameId": 1
                }
                """);

        mockMvc.perform(post("/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(factionRequest))
                .andExpect(status().is(201));

        String updatedFactionRequest = String.format("""
                {
                "name": "faction2",
                "description": "This is an updated example faction",
                "gameId": 1
                }
                """);

        mockMvc.perform(put("/factions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedFactionRequest))
                .andExpect(status().isOk());

        mockMvc.perform(get("/factions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("faction2"))
                .andExpect(jsonPath("$.description").value("This is an updated example faction"));
    }

    @Test
    public void deleteFaction() throws Exception {
        Integer user2Id = createUser("user2");
        createGame(user2Id, "game2");
        String factionRequest = String.format("""
                {
                "name": "faction1",
                "description": "This is an example faction",
                "gameId": 1
                }
                """);

        mockMvc.perform(post("/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(factionRequest))
                .andExpect(status().is(201));

        mockMvc.perform(delete("/factions/1"))
                .andExpect(status().is(204));

        mockMvc.perform(get("/factions/1"))
                .andExpect(status().is(404));
    }
}
