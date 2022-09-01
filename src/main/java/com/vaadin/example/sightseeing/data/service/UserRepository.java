package com.vaadin.example.sightseeing.data.service;

import com.vaadin.example.sightseeing.data.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}