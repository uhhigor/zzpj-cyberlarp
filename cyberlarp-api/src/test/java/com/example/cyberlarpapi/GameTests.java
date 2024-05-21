package com.example.cyberlarpapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameTests {
    @Autowired
    private MockMvc mockMvc;

    // Scenario 1: Create a new game
    // 1. Create new user
    // 2. Create new game with the user as the game master
    @Test
    public void createGame() {
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
    }

    // Scenario 2: Get game by id
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Get the game by id
    @Test
    public void getGameById() {
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
                    .andExpect(jsonPath("$.message").value("Game created successfully"))
                    .andExpect(jsonPath("$.game.id").exists())
                    .andExpect(jsonPath("$.game.name").value("Game 1"))
                    .andExpect(jsonPath("$.game.description").value("This is an example game"))
                    .andExpect(jsonPath("$.game.gameMasterId").value(1));


        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/game/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").doesNotExist())
                    .andExpect(jsonPath("$.game.id").exists())
                    .andExpect(jsonPath("$.game.name").value("Game 1"))
                    .andExpect(jsonPath("$.game.description").value("This is an example game"))
                    .andExpect(jsonPath("$.game.gameMasterId").value(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    // Scenario 3: Get all games
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Create another new user
    // 4. Create another new game with the second user as the game master
    // 5. Get all games

    @Test
    public void getAllGames() {
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

        String gameRequest2 = """
                {
                "name": "Game 2",
                "description": "This is another example game",
                "gameMasterUserId": 2
                }
                """;

        try {
            mockMvc.perform(post("/game")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gameRequest2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Game created successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            ResultActions resultActions = mockMvc.perform(get("/game")
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions.andDo(print());
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..game.id").isArray())
                    .andExpect(jsonPath("$.[0].game.id").value(1))
                    .andExpect(jsonPath("$.[1].game.id").value(2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

    }

    //Scenario 4: Update game text data
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Update the game text data
    // 4. Get the game by id

    @Test
    public void updateGame() {
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

        String gameUpdateRequest = """
                {
                "name": "Game 1 updated",
                "description": "This is an updated example game",
                "gameMasterUserId": 1
                }
                """;

        try {
            mockMvc.perform(put("/game/1/textData")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gameUpdateRequest))
                    .andExpect(status().isOk()) //here
                    .andExpect(jsonPath("$.message").value("Game updated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/game/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").doesNotExist())
                    .andExpect(jsonPath("$.game.id").exists())
                    .andExpect(jsonPath("$.game.name").value("Game 1 updated"))
                    .andExpect(jsonPath("$.game.description").value("This is an updated example game"))
                    .andExpect(jsonPath("$.game.gameMasterId").value(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    //Scenario 5: Delete game
    // 1. Create new user
    // 2. Create new game with the user as the game master
    // 3. Delete the game
    // 4. Get all games

    @Test
    public void deleteGame() {
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

        try {
            mockMvc.perform(delete("/game/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Game deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            ResultActions resultActions = mockMvc.perform(get("/game")
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions.andDo(print());
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

        //Scenario 6: Make user owner of game
        // 1. Create new user
        // 2. Create new game with the user as the game master
        // 3. Create new user
        // 4. Make the second user the owner of the game
        // 5. Get the game by id

        @Test
        public void makeUserOwnerOfGame() {
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

            String makeOwnerRequest = """
                    {
                    "gameId": 1,
                    "userId": 2
                    }
                    """;

            try {
                mockMvc.perform(put("/game/1/gameMaster/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(makeOwnerRequest))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("User is now the owner of the game"));
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception thrown", e);
            }

            try {
                mockMvc.perform(get("/game/1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").doesNotExist())
                        .andExpect(jsonPath("$.game.gameMasterId").value(2));
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception thrown", e);
            }
        }

        //Scenario 7: Get all characters of the game
        // 1. Create new user
        // 2. Create new game with the user as the game master
        // 3. Get all characters of the game

        @Test
        public void getCharactersOfGame() {
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

            try {
                ResultActions resultActions = mockMvc.perform(get("/game/1")
                                .contentType(MediaType.APPLICATION_JSON));

                resultActions.andDo(print());
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.game.availableCharacterIds[0]").value(1)) //here
                        .andExpect(jsonPath("$.game.availableCharacterIds[1]").value(2))
                        .andExpect(jsonPath("$.game.availableCharacterIds[2]").value(3));
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception thrown", e);
            }
        }
}
