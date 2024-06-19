package com.example.cyberlarpapi.e2e;

import com.example.cyberlarpapi.e2e.secutity.CustomSecurityPostProcessor;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MessageTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;
    private MvcResult character1;
    private MvcResult character2;
    private MvcResult game;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/").with(CustomSecurityPostProcessor.applySecurityForUser1()))
                .alwaysDo(print())
                .build();
        createUser("user1");
        createUser("user2");
        createGame();
        createCharacters();
        assignCharactersToUsers();
    }

    private void createUser(String username) throws Exception {
        String userRequest = String.format("{\"username\": \"%s\"}", username);
        mockMvc.perform(get("/users/user")
                        .with(CustomSecurityPostProcessor.applySecurity(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    private void createGame() throws Exception {
        String gameRequest = """
                {
                "name": "Game 1",
                "description": "This is an example game",
                "gameMasterUserId": 1
                }
                """;
        game = mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gameRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Game created successfully"))
                .andReturn();
    }

    private void createCharacters() throws Exception {
        createCharacter("Character 1", "FIXER");
        createCharacter("Character 2", "PUNK");
    }

    private void createCharacter(String name, String characterClass) throws Exception {
        String characterRequest = String.format("""
                {
                "name": "%s",
                "description": "This is an example character",
                "characterClass": "%s",
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
                """, name, characterClass);
        mockMvc.perform(post("/game/" + 1 + "/character/")
                        .with(CustomSecurityPostProcessor.applySecurity("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.id").exists());
    }

    private void assignCharactersToUsers() throws Exception {
        mockMvc.perform(post("/game/1/character/4/assignUser/1")
                        .with(CustomSecurityPostProcessor.applySecurity("user1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Character 4 assigned to user 1"));

        mockMvc.perform(post("/game/1/character/5/assignUser/2")
                        .with(CustomSecurityPostProcessor.applySecurity("user1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Character 5 assigned to user 2"));
    }

    @Test
    public void addMessageToGameTest() throws Exception {
        String messageRequest = """
                {
                "content": "This is a test message",
                "scope": "PUBLIC"
                }
                """;

        mockMvc.perform(post("/game/" + 1 + "/message/")
                        .with(CustomSecurityPostProcessor.applySecurity("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void getMessagesFromGameTest() throws Exception {
        String messageRequest = """
                {
                "content": "This is a test message",
                "scope": "PUBLIC"
                }
                """;

        mockMvc.perform(post("/game/" + 1 + "/message/")
                        .with(CustomSecurityPostProcessor.applySecurity("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isOk());

        mockMvc.perform(get("/game/" + 1 + "/message/PUBLIC")
                        .with(CustomSecurityPostProcessor.applySecurity("user1")))
                .andExpect(status().isOk());
    }
}
