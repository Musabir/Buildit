package com.rentit.user.service;

import com.rentit.user.domain.model.User;
import com.rentit.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User login(User user) {
       return userRepository.login(user);
    }
}
