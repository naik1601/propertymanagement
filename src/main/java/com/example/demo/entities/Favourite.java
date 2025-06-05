package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favourite")
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one: user who favorited the property
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    // Many-to-one: the property that was favorited
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private LocalDateTime createdAt;

    // Constructors
    public Favourite() {}

    public Favourite(Long id, User buyer, Property property, LocalDateTime createdAt) {
        this.id = id;
        this.buyer = buyer;
        this.property = property;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

