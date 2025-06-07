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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
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
        return propertyRepository.findByAgent(agent);

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

    public void updateProperty(Long id, Property updated, MultipartFile[] newImages) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        String oldTitle = property.getTitle();
        String newTitle = updated.getTitle();

        // Check and rename folder if title has changed
        if (!oldTitle.equals(newTitle)) {
            renamePropertyImageFolder(oldTitle, newTitle);
        }

        // Update basic property details
        property.setTitle(newTitle);
        property.setPrice(updated.getPrice());
        property.setLocation(updated.getLocation());
        property.setSize(updated.getSize());
        property.setDescription(updated.getDescription());

        // Add new images
        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String filename = storeProfilePicture(file, newTitle);
                    PropertyImage image = new PropertyImage();
                    image.setImageFileName(filename);
                    image.setProperty(property);
                    property.getImages().add(image);
                }
            }
        }

        propertyRepository.save(property);
    }


    @Override
    public Property getpropertybyid(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + id));
    }
@Override
    public void deletepropertyimgbyid(Long id) {
        PropertyImage image = propertyImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // delete physical file
        String path = "src/main/resources/static/images/PropertyImages/" +
                image.getProperty().getTitle() + "/" + image.getImageFileName();
        new File(path).delete();

    propertyImageRepository.delete(image);
    }


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
    public String storeProfilePicture(MultipartFile file, String propertyTitle){
        try {
            String uploadDir = "src/main/resources/static/images/PropertyImages/" + propertyTitle;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);


            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    private void renamePropertyImageFolder(String oldTitle, String newTitle) {
        String baseDir = "src/main/resources/static/images/PropertyImages/";
        File oldFolder = new File(baseDir + oldTitle);
        File newFolder = new File(baseDir + newTitle);

        if (oldFolder.exists() && !newFolder.exists()) {
            boolean success = oldFolder.renameTo(newFolder);
            if (!success) {
                throw new RuntimeException("Failed to rename folder from " + oldTitle + " to " + newTitle);
            }
        }
    }
    @Override
    public void deleteproperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        if (property.getImages() != null) {
            propertyImageRepository.deleteAll(property.getImages());
        }
        String folderPath = "src/main/resources/static/images/PropertyImages/" + property.getTitle();
        File directory = new File(folderPath);
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
            directory.delete();
        }
        User agent = property.getAgent();
        if (agent != null) {
            agent.getProperties().remove(property);
        }
        propertyRepository.delete(property);
    }



}
