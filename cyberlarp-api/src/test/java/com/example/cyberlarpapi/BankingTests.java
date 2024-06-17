package com.example.cyberlarpapi;


import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BankingTests {
    @Autowired
    private MockMvc mockMvc;
    MvcResult character1;
    MvcResult character2;
    MvcResult game;

    @BeforeEach
    public void setup() {
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

        userRequest = """
                {
                "username": "user2"
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
           "userId": "1",
           "gameId": "1",
           "name": "Character 1",
           "description": "This is an example character",
           "characterClass": "PUNK",
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
            """;

        String character2Request = """
           {
           "userId": "2",
           "gameId": "1",
           "name": "Character 1",
           "description": "This is an example character",
           "characterClass": "PUNK",
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
            """;

        try {
            character1 = mockMvc.perform(post("/characters/game/1")
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
            character2 = mockMvc.perform(post("/characters/game/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(character2Request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.character.id").exists())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }


    @Test
    public void createTransactionTest() throws UnsupportedEncodingException {
        String accountNumber1 = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.accountNumber");
        String accountNumber2 = JsonPath.read(character2.getResponse().getContentAsString(), "$.character.accountNumber");

        String transactionRequest = """
        {
        "senderBankAccount": \"""" + accountNumber1 + """
        ",
        "receiverBankAccount": \"""" + accountNumber2 + """
        ",
        "amount": 100,
        "gameId": 1
        }
        """;

        try {
            mockMvc.perform(post("/characters/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(transactionRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transaction.id").exists())

                    .andExpect(jsonPath("$.transaction.amount").value(100));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void bankAccountsBalanceAfterTransferTest() throws UnsupportedEncodingException {
        String accountNumber1 = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.accountNumber");
        String accountNumber2 = JsonPath.read(character2.getResponse().getContentAsString(), "$.character.accountNumber");
        Integer id1 = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.id");
        Integer id2 = JsonPath.read(character2.getResponse().getContentAsString(), "$.character.id");

        String transactionRequest = """
        {
        "senderBankAccount": \"""" + accountNumber1 + """
        ",
        "receiverBankAccount": \"""" + accountNumber2 + """
        ",
        "amount": 100,
        "gameId": 1
        }
        """;

        try {
            mockMvc.perform(post("/characters/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(transactionRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transaction.id").exists())
                    .andExpect(jsonPath("$.transaction.amount").value(100));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/characters/" + id1))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.character.balance").value(900));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(get("/characters/" + id2))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.character.balance").value(1100));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void notEnoughMoneyTest() throws UnsupportedEncodingException {
        String accountNumber1 = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.accountNumber");
        String accountNumber2 = JsonPath.read(character2.getResponse().getContentAsString(), "$.character.accountNumber");

        String transactionRequest = """
        {
        "senderBankAccount": \"""" + accountNumber1 + """
        ",
        "receiverBankAccount": \"""" + accountNumber2 + """
        ",
        "amount": 2000,
        "gameId": 1
        }
        """;

        try {
            mockMvc.perform(post("/characters/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(transactionRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Not enough money on your bank account!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void badAccountNumberTest() throws UnsupportedEncodingException {
        String accountNumber1 = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.accountNumber");
        String accountNumber2 = JsonPath.read(character2.getResponse().getContentAsString(), "$.character.accountNumber");

        String transactionRequest1 = """
        {
        "senderBankAccount": \"""" + accountNumber1 + """
        2",
        "receiverBankAccount": \"""" + accountNumber2 + """
        ",
        "amount": 100,
        "gameId": 1
        }
        """;

        String transactionRequest2 = """
        {
        "senderBankAccount": \"""" + accountNumber1 + """
        ",
        "receiverBankAccount": \"""" + accountNumber2 + """
        b",
        "amount": 100,
        "gameId": 1
        }
        """;

        try {
            mockMvc.perform(post("/characters/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(transactionRequest1))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("There is no character with given account number!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        try {
            mockMvc.perform(post("/characters/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(transactionRequest2))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("There is no character with given account number!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }
    }

    @Test
    public void charactersAreFromDifferentGameTest() throws UnsupportedEncodingException {
        String userRequest = """
                {
                "username": "user2"
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
                "name": "Game 2",
                "description": "This is an example game 2",
                "gameMasterUserId": 2
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

        String character3Request = """
           {
           "userId": 2,
           "gameId": 2,
           "name": "Character 1",
           "description": "This is an example character",
           "characterClass": "PUNK",
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
            """;
        MvcResult character3 = null;
        try {
            character3 = mockMvc.perform(post("/characters/game/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(character3Request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.character.id").exists())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

        String accountNumber1 = JsonPath.read(character1.getResponse().getContentAsString(), "$.character.accountNumber");
        assert character3 != null;
        String accountNumber3 = JsonPath.read(character3.getResponse().getContentAsString(), "$.character.accountNumber");

        String transactionRequest1 = """
        {
        "senderBankAccount": \"""" + accountNumber1 + """
        ",
        "receiverBankAccount": \"""" + accountNumber3 + """
        ",
        "amount": 100,
        "gameId": 1
        }
        """;

        try {
            mockMvc.perform(post("/characters/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(transactionRequest1))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Characters are not in the same game!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown", e);
        }

    }
    
}

