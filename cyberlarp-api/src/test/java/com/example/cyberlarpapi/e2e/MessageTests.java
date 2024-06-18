package com.example.cyberlarpapi.e2e;

import com.example.cyberlarpapi.e2e.secutity.CustomSecurityPostProcessor;
import com.jayway.jsonpath.JsonPath;
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

import java.io.UnsupportedEncodingException;

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
    MvcResult character1;
    MvcResult character2;
    MvcResult game;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/").with(CustomSecurityPostProcessor.applySecurityForUser1()))
                .alwaysDo(print())
                .build();

        String userRequest = """
                {
                "username": "user1"
                }
                """;

        try {
            mockMvc.perform(get("/users/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        userRequest = """
                {
                "username": "user2"
                }
                """;

        try {
            mockMvc.perform(get("/users/user")
                            .with(CustomSecurityPostProcessor.applySecurityForUser2())
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
            game = mockMvc.perform(post("/game")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gameRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Game created successfully"))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String character1Request = """
           {
           "name": "Character 1",
           "description": "This is an example character",
           "characterClass": "PUNK",
           "faction": "ANARCHISTS",
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

        String character2Request = """
           {
           "name": "Character 2",
           "description": "This is an example character",
           "characterClass": "PUNK",
           "faction": "ANARCHISTS",
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

        try {
            character1 = mockMvc.perform(post("/game/1/character/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(character1Request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.character.id").exists())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            character2 = mockMvc.perform(post("/game/1/character/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(character2Request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.character.id").exists())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
        //assign one character to user 1 and 2nd to user2
        try {
            mockMvc.perform(post("/game/1/character/" + JsonPath.read(character1.getResponse().getContentAsString(), "$.character.id") + "/assignUser/1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
        try {
            mockMvc.perform(post("/game/1/character/" + JsonPath.read(character2.getResponse().getContentAsString(), "$.character.id") + "/assignUser/2"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void addMessageToGameTest() throws UnsupportedEncodingException {
        Integer characterId = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.id");
        Integer gameId = JsonPath.read(game.getResponse().getContentAsString(), "$.game.id");
        String content = "This is a test message";

        try {
            mockMvc.perform(put("/game/" + gameId + "/message/" + content + "/PUBLIC/" + characterId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Message added to game successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void deleteMessageFromGameTest() throws UnsupportedEncodingException {
        Integer characterId = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.id");
        Integer gameId = JsonPath.read(game.getResponse().getContentAsString(), "$.game.id");
        String content = "This is a test message to delete";
        MvcResult messageResult = null;

        try {
            messageResult = mockMvc.perform(put("/game/" + gameId + "/message/" + content + "/PUBLIC/" + characterId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Message added to game successfully"))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        assert messageResult != null;
        Integer messageId = JsonPath.read(messageResult.getResponse().getContentAsString(), "$.message.id");

        try {
            mockMvc.perform(delete("/game/" + gameId + "/message/" + messageId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Message deleted from game successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void getMessagesFromGameTest() throws UnsupportedEncodingException {
        Integer characterId = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.id");
        Integer gameId = JsonPath.read(game.getResponse().getContentAsString(), "$.game.id");
        String content1 = "This is a public message";
        String content2 = "This is a faction message";

        try {
            mockMvc.perform(put("/game/" + gameId + "/message/" + content1 + "/PUBLIC/" + characterId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Message added to game successfully"));

            mockMvc.perform(put("/game/" + gameId + "/message/" + content2 + "/ANARCHISTS/" + characterId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Message added to game successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/game/" + gameId + "/messages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages.length()").value(2))
                    .andExpect(jsonPath("$.messages[0].content").value(content1))
                    .andExpect(jsonPath("$.messages[1].content").value(content2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }
}
