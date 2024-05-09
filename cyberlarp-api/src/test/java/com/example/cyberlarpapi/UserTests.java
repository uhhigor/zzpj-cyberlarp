package com.example.cyberlarpapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import java.util.Optional;

@SpringBootTest
public class UserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveUserWithSameEmailTest() {
        User user = new User();
        user.setEmail("12@gmail.com");

        User user2 = new User();
        user2.setEmail("12@gmail.com");

        Mockito.when(userRepository.save(user)).thenReturn(user);

        User savedUser = userRepository.save(user);
        Assert.notNull(savedUser, "User saved successfully");

        User savedUser2 = userRepository.save(user2);
        Assert.isNull(savedUser2, "User with same email cannot be saved");
    }

    @Test
    public void getUserTest() {
        User user = new User();
        user.setEmail("123@gmail.com");

        Mockito.when(userRepository.findByEmail("123@gmail.com")).thenReturn(Optional.of(user));

        Optional<User> foundUserOptional = userRepository.findByEmail("123@gmail.com");
        Assert.isTrue(foundUserOptional.isPresent(), "User found successfully");

        User foundUser = foundUserOptional.get();
        Assert.isTrue(foundUser.getEmail().equals("123@gmail.com"), "Emails are the same");

        Optional<User> notFoundUserOptional = userRepository.findByEmail("12@gmail.com");
        Assert.isTrue(notFoundUserOptional.isEmpty(), "User not found");
    }

}

