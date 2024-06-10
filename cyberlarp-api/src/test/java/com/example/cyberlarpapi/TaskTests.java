package com.example.cyberlarpapi;


import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.services.CharacterService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CharacterService characterService;

    // Scenario: Create a task throw exceptions
    @Test
    void createTaskThrowExceptions() throws Exception {
        Mockito.when(characterService.getById(1)).thenThrow(CharacterNotFoundException.class);
        mockMvc.perform(post("/task/create")
                .contentType("application/json")
                .content("{\"characterId\": 1, \"gameId\": 1, \"name\": \"Task 1\", \"description\": \"Description 1\", \"status\": \"INCOMPLETE\", \"type\": \"Type 1\", \"location\": \"Location 1\", \"reward\": \"Reward 1\", \"deadline\": \"2021-12-31\", \"completionDate\": \"2021-12-31\", \"completionTime\": \"12:00\"}"))
                .andExpect(status().isNotFound());
    }

}
