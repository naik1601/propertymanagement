package com.example.demo.services;

import com.example.demo.entities.Property;
import com.example.demo.entities.PropertyImage;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import com.example.demo.repositories.PropertyRepository;
import com.example.demo.repositories.PropertyImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.utils.CurrentUserContext;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,PropertyRepository propertyRepository,PropertyImageRepository propertyImageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.propertyRepository=propertyRepository;
        this.propertyImageRepository=propertyImageRepository;
    }

    private CurrentUserContext getCurrentUserContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + username));
        return new CurrentUserContext(user, auth);
    }

    @Override
    public void prepareDashboardModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("user", context.user());
        model.addAttribute("authorization", context.auth());
    }

    @Override
    public void prepareProfileModel(Model model) {
        model.addAttribute("user", getCurrentUserContext().user());
    }

    @Override
    public void prepareeditprofile(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        User user = context.user();
        Authentication auth = context.auth();
        model.addAttribute("user", user);
    }

    @Override
    public void updateUserSettings(User updatedUser) {
        User user = getCurrentUserContext().user();
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        userRepository.save(user);
    }

    @Override
    public User registerNewUser(User user, String roleNames) {
        UserRole role = UserRole.valueOf(roleNames.toUpperCase());

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByLastNameAsc();
    }

    @Override
    public List<Property> getpropertiesforagent() {
        User agent=getCurrentUser();
        if (!agent.getRole().equals(UserRole.AGENT)) {
            throw new IllegalStateException("User is not an agent.");
        }
        return agent.getProperties();

    }

    @Override
    public Property addNewProperty(Property property, MultipartFile[] images, User agent) {
        property.setAgent(agent);
        Property savedProperty = propertyRepository.save(property);
        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path imagePath = Paths.get("src/main/resources/static/images/PropertyImages",
                            property.getTitle(), filename);
                    System.out.println("Image saved at: /images/PropertyImages/" + imagePath + "/" + filename);
                    try {
                        Files.createDirectories(imagePath.getParent());
                        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                        PropertyImage propertyImage = new PropertyImage(null, filename, savedProperty);
                        propertyImageRepository.save(propertyImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return savedProperty;
    }
    @Override
    public Property findById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + id));
    }

//    @Override
//    public List<User> getTeamForCurrentManager() {
//        return userRepository.findByManager(getCurrentUserContext().user());
//    }

    @Override
    public void updateUser(User savedUser) {
        userRepository.save(savedUser);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }


}
