package com.example.demo.repositories;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(String email);
    List<User> findByRole(UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    List<User> findAllByOrderByLastNameAsc();



}
