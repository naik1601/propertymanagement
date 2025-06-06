package com.example.demo.controllers;

import com.example.demo.entities.Property;
import com.example.demo.entities.User;
import com.example.demo.services.AuthService;
import com.example.demo.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PropertyManageController {

    private final AuthService authService;
    private final UserService userService;

    public PropertyManageController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping({"/", "/index"})
    public String showIndex() {
        return "index";
    }

    // === LOGIN ===
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") User user,
                               HttpServletResponse response,
                               Model model) {
        try {
            Cookie jwtCookie = authService.loginAndCreateJwtCookie(user);
            response.addCookie(jwtCookie);
            return "redirect:/dashboard";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
    @GetMapping("/logout")
    @PreAuthorize("hasAnyRole('BUYER','AGENT','ADMIN')")
    public String logout(HttpServletResponse response) {
        authService.clearJwtCookie(response);
        return "redirect:/login";
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('BUYER','AGENT','ADMIN')")
    public String showDashboard(Model model) {
        userService.prepareDashboardModel(model);
        return "dashboard";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("selectedRoles") String roleNames,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            User savedUser = userService.registerNewUser(user, roleNames);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('BUYER','AGENT','ADMIN')")
    public String showProfile(Model model) {
        userService.prepareProfileModel(model);
        return "profile";
    }

    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('BUYER','AGENT','ADMIN')")
    public String showSettings(Model model) {
        userService.prepareeditprofile(model);
        return "editprofile";
    }
    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('BUYER','AGENT','ADMIN')")
    public String updateSettings(@ModelAttribute("user") User updatedUser,
                                 RedirectAttributes redirectAttributes) {
        try {
            User actualUser = userService.getCurrentUser();
            actualUser.setFirstName(updatedUser.getFirstName());
            actualUser.setLastName(updatedUser.getLastName());
            actualUser.setEmail(updatedUser.getEmail());
            userService.updateUserSettings(actualUser);
            redirectAttributes.addFlashAttribute("successMessage", "Account updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update account: " + ex.getMessage());
        }
        return "redirect:/profile";
    }
    @GetMapping("/properties/manage")
    @PreAuthorize("hasRole('AGENT')")
    public String showAgentDashboard(Model model) {
        List<Property> properties = userService.getpropertiesforagent();
        model.addAttribute("properties", properties);
        return "manageproperty";
    }
    @GetMapping("/properties/add")
    @PreAuthorize("hasRole('AGENT')")
    public String Addnewproperty(Model model) {
        model.addAttribute("property", new Property());
        return "addnewproperty";
    }

    @PostMapping("/properties/add")
    @PreAuthorize("hasRole('AGENT')")
    public String addProperty(@ModelAttribute Property property,
                              @RequestParam("file") MultipartFile[] images) {
        User agent = userService.getCurrentUser();
        userService.addNewProperty(property, images, agent);
        return "redirect:/properties/manage";
    }
    @GetMapping("/properties/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Property property = userService.findById(id);
        model.addAttribute("property", property);
        return "editproperty"; // the name of your Thymeleaf template
    }



}