package com.taskhub.project.core.user.repo;

import com.taskhub.project.core.user.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
