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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class GroupChatTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/").with(CustomSecurityPostProcessor.applySecurityForUser1()))
                .alwaysDo(print())
                .build();
        createUser("user1");
        createUser("user2");
        createGame();
        createFaction_1();
        createFaction_2();
        createCharacters();
    }

    private void createUser(String username) throws Exception {
        String userRequest = String.format("{\"username\": \"%s\"}", username);
        mockMvc.perform(get("/users/user")
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
        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gameRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Game created successfully"));
    }

    private void createCharacters() throws Exception {
        createCharacter(1, 1, "Character 1", "FIXER", 1);
        createCharacter(2, 1, "Character 2", "PUNK", 2);
    }

    private void createCharacter(int userId, int gameId, String name, String characterClass, int factionId) throws Exception {
        String characterRequest = String.format("""
                {
                "userId": "%d",
                "gameId": "%d",
                "name": "%s",
                "description": "This is an example character",
                "characterClass": "%s",
                "factionId": "%d",
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
                """, userId, gameId, name, characterClass, factionId);
        mockMvc.perform(post("/characters/game/" + gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.id").exists());
    }

    private void createFaction_1() throws Exception {
        String factionRequest = """
                {
                "name": "Faction 1",
                "description": "This is an example faction"
                }
                """;
        mockMvc.perform(post("/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(factionRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    private void createFaction_2() throws Exception {
        String factionRequest = """
                {
                "name": "Faction 2",
                "description": "This is an example faction"
                }
                """;
        mockMvc.perform(post("/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(factionRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    private void createGroupChat_set() throws Exception {
        String groupChatRequest = """
                {
                "gameId": 1,
                "ownerId": 1
                }
                """;
        mockMvc.perform(post("/groupChat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupChatRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    // Scenario 1: Create a new group chat with proper owner validation and error handling
    @Test
    void createGroupChat() {
        String groupChatRequest = """
                {
                    "gameId": 1,
                    "ownerId": 1
                }
                """;

        try {
            mockMvc.perform(post("/groupChat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(groupChatRequest))
                    .andExpect(status().isCreated());
                    //.andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 2: Invite character to group chat with same faction validation and error handling
    @Test
    void inviteCharacterToGroupChat() throws Exception {
        createGroupChat_set();
        createCharacter(1, 1, "Character 1", "FIXER", 1);
        String inviteCharacterRequest = """
                {
                    "characterId": 3
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteCharacterRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 3: Attempt to invite character from different faction
    @Test
    void inviteCharacterFromDifferentFaction() throws Exception {
        createGroupChat_set();

        String inviteCharacterRequest = """
                {
                    "characterId": 2
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteCharacterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Character does not belong to the same faction as the owner"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }


    }

    // Scenario 4: Attempt to invite character already in the group chat
    @Test
    void inviteCharacterAlreadyInGroupChat() throws Exception {
        createGroupChat_set();
        createCharacter(1, 1, "Character 1", "FIXER", 1);
        String inviteCharacterRequest = """
            {
                "characterId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteCharacterRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptCharacterRequest = """
            {
                "groupChatId": 1,
                "characterId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/accept")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(acceptCharacterRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteCharacterRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Character already in group chat"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


    // Scenario 5: Read messages from group chat
    @Test
    @Transactional
    void readMessagesFromGroupChat() throws Exception {
        createGroupChat_set();
        createCharacter(1, 1, "Character 1", "FIXER", 1);
        String inviteCharacterRequest = """
            {
                "characterId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteCharacterRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptInviteRequest = """
            {
                "groupChatId": 1,
                "characterId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/accept")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(acceptInviteRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String messageRequest = """
            {
            "content": "Siemanko",
            "senderId": 1
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/message")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(messageRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String messageRequest2 = """
            {
            "content": "Cześć",
            "senderId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/message")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(messageRequest2))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            String response = mockMvc.perform(get("/groupChat/1/messages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println("Response: " + response);
            mockMvc.perform(get("/groupChat/1/messages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].content").value("Siemanko"))
                    .andExpect(jsonPath("$[0].senderId").value(1))
                    .andExpect(jsonPath("$[1].content").value("Cześć"))
                    .andExpect(jsonPath("$[1].senderId").value(3));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


    //Scenario 6 accept invite to chatGroup
    @Test
    void acceptInviteToGroupChat() throws Exception {
        createGroupChat_set();
        createCharacter(1, 1, "Character 3", "FIXER", 1);

        String inviteCharacterRequest = """
            {
                "characterId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteCharacterRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during invite", e);
        }

        String acceptInviteRequest = """
            {
                "characterId": 3
            }
            """;

        try {
            mockMvc.perform(post("/groupChat/1/accept")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(acceptInviteRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during accept", e);
        }
    }
}