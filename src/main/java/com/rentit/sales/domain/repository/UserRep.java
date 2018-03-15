package com.rentit.sales.domain.repository;

import com.rentit.sales.domain.model.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRep extends JpaRepository<UserTable, String> {


}
