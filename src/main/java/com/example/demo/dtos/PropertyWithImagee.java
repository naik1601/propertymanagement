package com.example.demo.dtos;

import com.example.demo.entities.Property;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyWithImagee {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String location;
    private Integer size;
    private List<String> imageFileNames;

    public PropertyWithImagee(){

    }
    public PropertyWithImagee(Property property) {
        this.id = property.getId();
        this.title = property.getTitle();
        this.price = property.getPrice();
        this.description = property.getDescription();
        this.location = property.getLocation();
        this.size = property.getSize();
        this.imageFileNames = property.getImages().stream()
                .map(img -> "/images/PropertyImages/" + img.getImageFileName())
                .collect(Collectors.toList());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getImageFileNames() {
        return imageFileNames;
    }

    public void setImageFileNames(List<String> imageFileNames) {
        this.imageFileNames = imageFileNames;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
