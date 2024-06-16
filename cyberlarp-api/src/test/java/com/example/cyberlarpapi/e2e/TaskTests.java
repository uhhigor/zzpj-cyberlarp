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
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskTests {

    @Autowired
    private MockMvc mockMvc;

    private MvcResult character1;
    private MvcResult character2;
    private MvcResult game;

    @BeforeEach
    void setup() throws Exception {
        createUser("user1");
        createUser("user2");
        createGame();
        createCharacters();
    }

    private void createUser(String username) throws Exception {
        String userRequest = String.format("{\"username\": \"%s\"}", username);
        mockMvc.perform(post("/users")
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
        createCharacter(1, 1, "Character 1", "FIXER");
        createCharacter(2, 1, "Character 2", "PUNK");
    }

    private void createCharacter(int userId, int gameId, String name, String characterClass) throws Exception {
        String characterRequest = String.format("""
                {
                "userId": "%d",
                "gameId": "%d",
                "name": "%s",
                "description": "This is an example character",
                "characterClass": "%s",
                "factionId": null,
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
                """, userId, gameId, name, characterClass);
        mockMvc.perform(post("/characters/game/" + gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(characterRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.id").exists());
    }

    private void createTask() throws Exception {
        String taskRequest = """
                {
                "characterId": 1,
                "name": "Task 1",
                "description": "This is an example task",
                "status": "IN_PROGRESS",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0,
                "deadline": "2022-12-31",
                "completionDate": "2022-12-31",
                "completionTime": "12:00"
                }
                """;

         mockMvc.perform(post("/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id").exists())
                .andReturn();
    }



    // Create a task bad request
    @Test
    @WithMockUser(username = "user2")
    void createTask_Not_Fixer() {
        String taskRequest = """
            {
            "characterId": 2,
            "name": "Task 1",
            "description": "This is an example task",
            "status": "IN_PROGRESS",
            "type": "DELIVERY",
            "location": "Location 1",
            "reward": 100.0,
            "deadline": "2022-12-31",
            "completionDate": "2022-12-31",
            "completionTime": "12:00"
            }
            """;

        try {
            mockMvc.perform(post("/task/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Only Fixers can create or update tasks"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


    // Create a task success
    @Test
    @WithMockUser(username = "user1")
    void createTask_Success() {
        String taskRequest = """
                {
                "characterId": 1,
                "name": "Task 1",
                "description": "This is an example task",
                "status": "IN_PROGRESS",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0,
                "deadline": "2022-12-31",
                "completionDate": "2022-12-31",
                "completionTime": "12:00"
                }
                """;

        try {
            mockMvc.perform(post("/task/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.character.id").value(1))
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.deadline").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionDate").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionTime").value("12:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Create a task bad request
    @Test
    @WithMockUser(username = "user1")
    void createTask_Bad_Character() {
        String taskRequest = """
                {
                "characterId": 2,
                "name": "Task 1",
                "description": "This is an example task",
                "status": "IN_PROGRESS",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0,
                "deadline": "2022-12-31",
                "completionDate": "2022-12-31",
                "completionTime": "12:00"
                }
                """;

        try {
            mockMvc.perform(post("/task/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Not your character"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Create a task bad request
    @Test
    void createTask_No_Login() {
        String taskRequest = """
            {
            "characterId": 1,
            "name": "Task 1",
            "description": "This is an example task",
            "status": "IN_PROGRESS",
            "type": "DELIVERY",
            "location": "Location 1",
            "reward": 100.0,
            "deadline": "2022-12-31",
            "completionDate": "2022-12-31",
            "completionTime": "12:00"
            }
            """;

        try {
            mockMvc.perform(post("/task/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Not logged in"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


    // Update a task success
    @Test
    @WithMockUser(username = "user1")
    void updateTask_Success() throws Exception {
        createTask();
        String taskRequest = """
                {
                "characterId": 1,
                "name": "Task 3",
                "description": "This is an example task",
                "status": "IN_PROGRESS",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0,
                "deadline": "2022-12-31",
                "completionDate": "2022-12-31",
                "completionTime": "13:00"
                }
                """;

        try {
            mockMvc.perform(post("/task/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.character.id").value(1))
                    .andExpect(jsonPath("$.task.name").value("Task 3"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.deadline").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionDate").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionTime").value("13:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Update a task bad request
    @Test
    @WithMockUser(username = "user1")
    void updateTask_Task_Not_Found() {
        String taskRequest = """
                {
                "characterId": 1,
                "name": "Task 3",
                "description": "This is an example task",
                "status": "IN_PROGRESS",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0,
                "deadline": "2022-12-31",
                "completionDate": "2022-12-31",
                "completionTime": "13:00"
                }
                """;

        try {
            mockMvc.perform(post("/task/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // get a task success
    @Test
    @WithMockUser(username = "user1")
    void getTask_Success() throws Exception {
        createTask();

        try {
            mockMvc.perform(get("/task/get/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.character.id").value(1))
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.deadline").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionDate").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionTime").value("12:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // get a task bad request
    @Test
    @WithMockUser(username = "user1")
    void getTask_Task_Not_Found() {
        try {
            mockMvc.perform(get("/task/get/1"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // delete a task success
    @Test
    @WithMockUser(username = "user1")
    void deleteTask_Success() throws Exception {
        createTask();

        try {
            mockMvc.perform(post("/task/delete/1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // delete a task bad request
    @Test
    @WithMockUser(username = "user1")
    void deleteTask_Task_Not_Found() {
        try {
            mockMvc.perform(post("/task/delete/1"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // complete a task success
    @Test
    @WithMockUser(username = "user1")
    void completeTask_Success() throws Exception {
        createTask();

        try {
            mockMvc.perform(post("/task/complete/1?reward=100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.character.id").value(1))
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.deadline").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionDate").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionTime").value("12:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // incomplete a task success
    @Test
    @WithMockUser(username = "user1")
    void incompleteTask_Success() throws Exception {
        createTask();

        try {
            mockMvc.perform(post("/task/incomplete/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.character.id").value(1))
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("FAILURE"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.deadline").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionDate").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionTime").value("12:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // assign a task success
    @Test
    @WithMockUser(username = "user1")
    void assignTask_Success() throws Exception {
        createTask();

        try {
            mockMvc.perform(post("/task/assign/1?characterId=2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.character.id").value(2))
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.deadline").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionDate").value("2022-12-31"))
                    .andExpect(jsonPath("$.task.completionTime").value("12:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


}
