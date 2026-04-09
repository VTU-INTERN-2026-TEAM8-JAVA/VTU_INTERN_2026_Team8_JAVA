package com.wealthwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wealthwise.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}