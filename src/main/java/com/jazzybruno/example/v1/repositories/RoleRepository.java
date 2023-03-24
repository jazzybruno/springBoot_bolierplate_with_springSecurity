package com.jazzybruno.example.v1.repositories;

import com.jazzybruno.example.v1.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Long , Role> {
}
