package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one: message sender
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String content;

    // Many-to-one: associated property
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private LocalDateTime timestamp;

    private String reply;

    // Constructors
    public Message() {}

    public Message(Long id, User sender, String content, Property property, LocalDateTime timestamp, String reply) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.property = property;
        this.timestamp = timestamp;
        this.reply = reply;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
}

