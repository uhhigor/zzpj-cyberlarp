package com.example.cyberlarpapi.game.services;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.UserRepository;
import com.example.cyberlarpapi.game.exceptions.UserException.UserException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public User getUserById(int id) throws UserServiceException {
        return userRepository.findById(id).orElseThrow(() -> new UserServiceException("User not found"));
    }

    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }
}
