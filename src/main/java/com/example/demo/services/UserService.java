package com.example.demo.services;

import com.example.demo.entities.Property;
import com.example.demo.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    @PreAuthorize("isAuthenticated()")
    void prepareDashboardModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void prepareProfileModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void prepareeditprofile(Model model);

    @PreAuthorize("isAuthenticated()")
    void updateUserSettings(User updatedUser);

    @PreAuthorize("hasRole('ADMIN')")
    List<User> getAllUsers();

    @PreAuthorize("hasRole('AGENT')")
    List<Property> getpropertiesforagent();

    @PreAuthorize("hasRole('AGENT')")
    Property addNewProperty(Property property, MultipartFile[] images, User agent);


//    @PreAuthorize("hasRole('MANAGER')")
//    List<User> getTeamForCurrentManager();


     User registerNewUser(User user, String roleNames);

    void updateUser(User savedUser);

    @PreAuthorize("isAuthenticated()")
    User getCurrentUser();

    @PreAuthorize("hasRole('AGENT')")
    Property findById(Long id);
}
