package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "property_images")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageFileName;

    // Many-to-one: property
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    // Constructors
    public PropertyImage() {}

    public PropertyImage(Long id, String imageFileName, Property property) {
        this.id = id;
        this.imageFileName = imageFileName;
        this.property = property;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageFileName() { return imageFileName; }
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
}

