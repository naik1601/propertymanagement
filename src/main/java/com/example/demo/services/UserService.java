package com.example.demo.services;

import com.example.demo.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

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

//    @PreAuthorize("hasRole('MANAGER')")
//    List<User> getTeamForCurrentManager();


     User registerNewUser(User user, String roleNames);

    void updateUser(User savedUser);

    @PreAuthorize("isAuthenticated()")
    User getCurrentUser();
}
