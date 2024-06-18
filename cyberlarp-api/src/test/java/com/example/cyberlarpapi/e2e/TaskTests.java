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
class TaskTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

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
        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gameRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Game created successfully"));
    }

    private void createCharacters() throws Exception {
        createCharacter("user1", 1, "Character 1", "FIXER");
        createCharacter("user1", 1, "Character 2", "PUNK");
    }

    private void createCharacter(String username, int gameId, String name, String characterClass) throws Exception {
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
        mockMvc.perform(post("/game/" + gameId + "/character/")
                        .with(CustomSecurityPostProcessor.applySecurity(username))
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

    private Integer createTask( String username) throws Exception {
        String taskRequest = """
            {
            "name": "Task 1",
            "description": "This is an example task",
            "type": "DELIVERY",
            "location": "Location 1",
            "reward": 100.0
            }
            """;


         MvcResult response = mockMvc.perform(post("/game/1/task/")
                        .with(CustomSecurityPostProcessor.applySecurity(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id").exists()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(response.getResponse().getContentAsString());

        return responseJson.get("task").get("id").asInt();

    }


    // Create a task bad request
    @Test
    @WithMockUser(username = "user2")
    void createTask_Not_Fixer() {
        String taskRequest = """
            {
            "name": "Task 1",
            "description": "This is an example task",
            "type": "DELIVERY",
            "location": "Location 1",
            "reward": 100.0
            }
            """;

        try {
            mockMvc.perform(post("/game/1/task/")
                            .with(CustomSecurityPostProcessor.applySecurity("user2"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Only Fixers can create tasks"));
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
                "name": "Task 1",
                "description": "This is an example task",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0
                }
                """;

        try {
            mockMvc.perform(post("/game/1/task/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Create a task bad request
    @Test
    @WithMockUser(username = "user1")
    void assignTask_Bad_Character() throws Exception {
        Integer taskId = createTask("user1");

        try {
            mockMvc.perform(post("/game/1/task/"+ taskId+"/assignCharacter/10"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


    // Update a task success
    @Test
    @WithMockUser(username = "user1")
    void updateTask_Success() throws Exception {
        Integer taskId = createTask( "user1");
        String taskRequest = """
                {
                "name": "Task 3",
                "description": "This is an example task",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0
                }
                """;

        try {
            mockMvc.perform(put("/game/1/task/" + taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 3"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0));
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
                "name": "Task 3",
                "description": "This is an example task",
                "type": "DELIVERY",
                "location": "Location 1",
                "reward": 100.0
                }
                """;

        try {
            mockMvc.perform(put("/game/1/task/10")
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
        Integer taskId = createTask ("user1");

        try {
            mockMvc.perform(get("/game/1/task/" + taskId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0));
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
            mockMvc.perform(get("/game/1/task/10"))
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
        Integer taskId =  createTask("user1");

        try {
            mockMvc.perform(post("/game/1/task/delete/" + taskId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Task deleted successfully"));
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
            mockMvc.perform(post("/task/delete/10"))
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
        Integer taskId = createTask( "user1");

        try {
            mockMvc.perform(post("/game/1/task/"+ taskId+"/assignCharacter/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.assignedCharacter.id").value(5));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(post("/game/1/task/"+taskId+"/status/SUCCESS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // incomplete a task success
    @Test
    @WithMockUser(username = "user1")
    void incompleteTask_Success() throws Exception {
        Integer taskId = createTask("user1");

        try {
            mockMvc.perform(post("/game/1/task/"+ taskId +"/status/FAILURE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("FAILURE"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // assign a task success
    @Test
    @WithMockUser(username = "user1")
    void assignTask_Success() throws Exception {
        Integer taskId = createTask("user1");

        try {
            mockMvc.perform(post("/game/1/task/"+ taskId+"/assignCharacter/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.assignedCharacter.id").value(5));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    @WithMockUser(username = "user1")
    void unassignTaskSuccessfully() throws Exception {
        Integer taskId =  createTask("user1");

        try {
            mockMvc.perform(post("/game/1/task/"+ taskId+"/assignCharacter/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"))
                    .andExpect(jsonPath("$.task.description").value("This is an example task"))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    .andExpect(jsonPath("$.task.type").value("DELIVERY"))
                    .andExpect(jsonPath("$.task.location").value("Location 1"))
                    .andExpect(jsonPath("$.task.reward").value(100.0))
                    .andExpect(jsonPath("$.task.assignedCharacter.id").value(5));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(post("/game/1/task/"+taskId+"/deassignCharacter"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.task.id").exists())
                    .andExpect(jsonPath("$.task.name").value("Task 1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    @WithMockUser(username = "user1")
    void getTasksForCharacter() throws Exception {
        createTask( "user1");
        createTask("user1");

        mockMvc.perform(get("/game/1/task/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

}
