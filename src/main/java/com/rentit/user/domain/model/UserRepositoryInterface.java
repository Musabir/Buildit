package com.rentit.user.domain.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepositoryInterface extends CrudRepository<Customer, Long> {
}