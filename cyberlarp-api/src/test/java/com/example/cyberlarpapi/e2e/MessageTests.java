package com.example.cyberlarpapi.e2e;

import com.example.cyberlarpapi.e2e.secutity.CustomSecurityPostProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        character1 = createCharacter("user1", 1, "Character 1", "FIXER");
        character2 = createCharacter("user1", 1, "Character 2", "PUNK");
    }

    private MvcResult createCharacter(String username, int gameId, String name, String characterClass) throws Exception {
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
        return mockMvc.perform(post("/game/" + gameId + "/character/")
                        .with(CustomSecurityPostProcessor.applySecurity(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.id").exists())
                .andReturn();
    }

    @Test
    public void addMessageToGameTest() throws Exception {
        int gameId = 1;
        String content = "This is a test message";
        MessageRequest messageRequest = new MessageRequest(content, "PUBLIC");

        mockMvc.perform(post("/game/" + gameId + "/message/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(messageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message added to game successfully"));
    }


    @Test
    public void deleteMessageFromGameTest() throws Exception {
        int gameId = 1;
        String content = "This is a test message to delete";
        MessageRequest messageRequest = new MessageRequest(content, "PUBLIC");

        MvcResult messageResult = mockMvc.perform(post("/game/" + gameId + "/message/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(messageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message added to game successfully"))
                .andReturn();

        int messageId = new ObjectMapper().readTree(messageResult.getResponse().getContentAsString()).get("id").asInt();

        mockMvc.perform(delete("/game/" + gameId + "/message/" + messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message deleted from game successfully"));
    }

    @Test
    public void getMessagesFromGameTest() throws Exception {
        int gameId = 1;
        String content1 = "This is a public message";
        String content2 = "This is a faction message";

        MessageRequest messageRequest1 = new MessageRequest(content1, "PUBLIC");
        MessageRequest messageRequest2 = new MessageRequest(content2, "ANARCHISTS");

        mockMvc.perform(post("/game/" + gameId + "/message/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(messageRequest1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message added to game successfully"));

        mockMvc.perform(post("/game/" + gameId + "/message/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(messageRequest2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message added to game successfully"));

        mockMvc.perform(get("/game/" + gameId + "/messages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(jsonPath("$.messages[0].content").value(content1))
                .andExpect(jsonPath("$.messages[1].content").value(content2));
    }

    public static class MessageRequest {
        public String content;
        public String scope;

        public MessageRequest(String content, String scope) {
            this.content = content;
            this.scope = scope;
        }
    }
}
