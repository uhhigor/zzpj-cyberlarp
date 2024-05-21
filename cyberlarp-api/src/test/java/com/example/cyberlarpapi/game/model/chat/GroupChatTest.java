package com.example.cyberlarpapi.game.model.chat;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GroupChatTest {
    @Autowired
    private MockMvc mockMvc;

    // Scenario 1: Create a new group chat
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new group chat with the user as the owner

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

        String groupChatRequest = """
                {
                
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

    // Scenario 2: Invite player to group chat
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new Player
    // 4. Create new group chat with the player as the owner
    // 5. Invite player to group chat

    @Test
    void invitePlayerToGroupChat() {
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

        String groupChatRequest = """
                {

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

        String inviteUserRequest = """
                {
                "userId": 1
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteUserRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 3: Accept invitation to group chat
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new group chat with the user as the owner
    // 4. Invite player to group chat
    // 5. Accept invitation to group chat

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

        String groupChatRequest = """
                {

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

        String inviteUserRequest = """
                {
                "userId": 1
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteUserRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptInviteRequest = """
                {
                "userId": 1
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

    // Scenario 4: Add message to group chat
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new group chat with the user as the owner
    // 4. Invite player to group chat
    // 5. Accept invitation to group chat
    // 6. Add message to group chat

    @Test
    void addMessageToGroupChat() {
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

        String groupChatRequest = """
                {

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

        String inviteUserRequest = """
                {
                "userId": 1
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteUserRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptInviteRequest = """
                {
                "userId": 1
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
    }


    // Scenario 5: Read messages from group chat
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new group chat with the user as the owner
    // 4. Invite player to group chat
    // 5. Accept invitation to group chat
    // 6. Add message to group chat
    // 7. Create new user
    // 8. Create new player2
    // 9. Invite player2 to group chat
    // 10. Accept invitation to group chat
    // 11. Add message to group chat
    // 12. Read messages from group chat

    @Test
    void readMessagesFromGroupChat() {
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

        String groupChatRequest = """
                {

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

        String inviteUserRequest = """
                {
                "userId": 1
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteUserRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptInviteRequest = """
                {
                "userId": 1
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

        String userRequest2 = """
                {
                "username": "user2"
                }
                """;

        try {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String inviteUserRequest2 = """
                {
                "userId": 2
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteUserRequest2))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptInviteRequest2 = """
                {
                "userId": 2
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/accept")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(acceptInviteRequest2))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String messageRequest2 = """
                {
                "content": "Hello",
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
                    .andExpect(jsonPath("$[1].content").value("Hello"))
                    .andExpect(jsonPath("$[1].senderId").value(2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

    }

    // Scenario 6: Remove old messages from group chat
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create new group chat with the user as the owner
    // 4. Invite player to group chat
    // 5. Accept invitation to group chat

    @Test
    void removeOldMessagesFromGroupChat() {
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

        String groupChatRequest = """
                {

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

        String inviteUserRequest = """
                {
                "userId": 1
                }
                """;

        try {
            mockMvc.perform(post("/groupChat/1/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inviteUserRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String acceptInviteRequest = """
                {
                "userId": 1
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

        try {
            mockMvc.perform(delete("/groupChat/1/messages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

    }
}