package com.example.demo.entities;


import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String username;

    // Relationships
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Property> properties;

//    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
//    private List<Message> sentMessages;
//
//    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
//    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Favourite> favourites;

    // Constructors
    public User() {}

    public User(Long id, String firstName, String lastName, String email, String password, UserRole role, LocalDateTime createdAt,String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.username=firstName;

    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }



    public LocalDateTime  getCreatedAt() { return createdAt; }
   public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //
    public List<Property> getProperties() { return properties; }
    public void setProperties(List<Property> properties) { this.properties = properties; }

//    public List<Message> getSentMessages() { return sentMessages; }
//    public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }
//
//    public List<Message> getReceivedMessages() { return receivedMessages; }
//    public void setReceivedMessages(List<Message> receivedMessages) { this.receivedMessages = receivedMessages; }

    public List<Favourite> getFavorites() { return favourites; }
    public void setFavorites(List<Favourite> favorites) { this.favourites = favourites; }
}
