package com.jazzybruno.example.v1.repositories;

import com.jazzybruno.example.v1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
