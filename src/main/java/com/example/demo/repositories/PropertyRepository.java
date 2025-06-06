package com.example.demo.repositories;

import com.example.demo.entities.Property;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findbyID(UserRole role);
}
