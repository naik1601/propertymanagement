package com.example.demo.repositories;

import com.example.demo.entities.PropertyImage;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

}
