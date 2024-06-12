package com.example.cyberlarpapi.game.services;

import java.util.Optional;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.repositories.UserRepository;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public _User save(_User user) {
        return userRepository.save(user);
    }

    public _User getUserById(int id) throws UserServiceException {
        return userRepository.findById(id).orElseThrow(() -> new UserServiceException("User not found"));
    }

    public Optional<_User> getUserByEmail(String email) throws UserServiceException {
        return userRepository.findByEmail(email);
    }

    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

}
