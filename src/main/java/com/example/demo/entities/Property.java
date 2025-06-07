package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private Integer size;

    // Many-to-one: agent (User)
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    // One-to-many: property images
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<PropertyImage> images;

    // Many-to-many: favorited by users
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Favourite> favourites;

    // Constructors
    public Property() {}

    public Property(Long id, String title, Double price, String description, String location, Integer size, User agent) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.location = location;
        this.size = size;
        this.agent = agent;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public User getAgent() { return agent; }
    public void setAgent(User agent) { this.agent = agent; }

    public List<PropertyImage> getImages() { return images; }
    public void setImages(List<PropertyImage> images) { this.images = images; }

    public List<Favourite> getFavorites() { return favourites; }
    public void setFavorites(List<Favourite> favorites) { this.favourites = favorites; }
}
