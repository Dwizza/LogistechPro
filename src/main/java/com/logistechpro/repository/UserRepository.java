package com.logistechpro.repository;

import com.logistechpro.models.Enums.Role;
import com.logistechpro.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);
}
