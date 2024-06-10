package com.example.cyberlarpapi;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // Scenario 1: Create a new group chat with proper owner validation and error handling
    @Test
    void createGroupChat() {
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

        String factionRequest = """
                {
                "name": "Faction 1",
                "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }


        String characterRequest = """
                {
                "name": "Character 1",
                "description": "This is an example character",
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 2: Invite character to group chat with same faction validation and error handling
    @Test
    void inviteCharacterToGroupChat() {
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

        String factionRequest = """
                {
                "name": "Faction 1",
                "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }


        String characterRequest = """
                {
                "name": "Character 1",
                "description": "This is an example character",
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest2 = """
                {
                "name": "Character 2",
                "description": "This is an example character",
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

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 2 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 2"))
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

        String inviteCharacterRequest = """
                {
                    "characterId": 2
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
    void inviteCharacterFromDifferentFaction() {
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

        String factionRequest = """
                {
                    "name": "Faction 1",
                    "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String factionRequest2 = """
                {
                    "name": "Faction 2",
                    "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest2))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.id").value("2"));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest = """
                {
                    "name": "Character 1",
                    "description": "This is an example character",
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest2 = """
                {
                    "name": "Character 2",
                    "description": "This is an example character",
                    "characterClass": "PUNK",
                    "factionId": 2,
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
                            .content(characterRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 2 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 2"))
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
    void inviteCharacterAlreadyInGroupChat() {
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

        String factionRequest = """
                {
                    "name": "Faction 1",
                    "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest = """
                {
                    "name": "Character 1",
                    "description": "This is an example character",
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest2 = """
                {
                    "name": "Character 2",
                    "description": "This is an example character",
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

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 2 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 2"))
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

        String inviteCharacterRequest = """
                {
                    "characterId": 2
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
                    "characterId": 2
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
    void readMessagesFromGroupChat() throws InterruptedException {
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

        String factionRequest = """
                {
                "name": "Faction 1",
                "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }


        String characterRequest = """
                {
                "name": "Character 1",
                "description": "This is an example character",
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest2 = """
                {
                "name": "Character 2",
                "description": "This is an example character",
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

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 2 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 2"))
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

        String inviteCharacterRequest = """
                {
                    "characterId": 2
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
                    "characterId": 1
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
                "senderId": 2
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
            mockMvc.perform(get("/groupChat/1/messages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].content").value("Siemanko"))
                    .andExpect(jsonPath("$[0].senderId").value(1))
                    .andExpect(jsonPath("$[1].content").value("Cześć"))
                    .andExpect(jsonPath("$[1].senderId").value(2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

    }

    //Scenario 6 accept invite to chatGroup
    @Test
    void acceptInviteToGroupChat() {
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

        String factionRequest = """
                {
                "name": "Faction 1",
                "description": "This is an example faction"
                }
                """;

        try {
            mockMvc.perform(post("/factions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(factionRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }


        String characterRequest = """
                {
                "name": "Character 1",
                "description": "This is an example character",
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String characterRequest2 = """
                {
                "name": "Character 2",
                "description": "This is an example character",
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

        try {
            mockMvc.perform(post("/characters/game/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(characterRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Character 2 added to game 1"))
                    .andExpect(jsonPath("$.character.id").exists())
                    .andExpect(jsonPath("$.character.name").value("Character 2"))
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

        String inviteCharacterRequest = """
                {
                    "characterId": 2
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
                    "characterId": 1
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
    }
}