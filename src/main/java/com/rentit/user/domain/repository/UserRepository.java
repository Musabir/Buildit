package com.rentit.user.domain.repository;

import com.rentit.user.domain.model.User;
import org.springframework.stereotype.Repository;

public interface UserRepository {
    User login(User user);
}
