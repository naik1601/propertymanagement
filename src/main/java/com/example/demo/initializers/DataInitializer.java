package com.example.demo.initializers;


import com.example.demo.entities.Property;
import com.example.demo.entities.PropertyImage;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.PropertyImageRepository;
import com.example.demo.repositories.PropertyRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,PropertyRepository propertyRepository,PropertyImageRepository propertyImageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.propertyRepository=propertyRepository;
        this.propertyImageRepository=propertyImageRepository;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {

            User u1 = new User(
                    null,
                    "Alice",
                    "Agent",
                    "alice@example.com",
                    passwordEncoder.encode("al.123"),
                    UserRole.BUYER,
                    LocalDateTime.now(),
                    "Alice");

            User u9 = new User(null,
                    "Grace",
                    "Mitchell",
                    "grace.mitchell@email.com",
                    passwordEncoder.encode("gm.123"),
                    UserRole.BUYER,
                    LocalDateTime.now(),
                    "Grace");
            User u8 = new User(null,
                    "mason",
                    "lee",
                    "mason.lee@email.com",
                    passwordEncoder.encode("ml.123"),
                    UserRole.AGENT,
                    LocalDateTime.now(),
                    "mason");
            User u7 = new User(null,
                    "Olivia",
                    "Bennett",
                    "Olivia.Bennett@email.com",
                    passwordEncoder.encode("ob.123"),
                    UserRole.AGENT,
                    LocalDateTime.now(),
                    "Olivia");
            User u6 = new User(null,
                    "Ava",
                    "Thompson",
                    "Ava.Thompson@email.com",
                    passwordEncoder.encode("at.123"),
                    UserRole.BUYER,
                    LocalDateTime.now(),
                    "Ava");
            User u5 = new User(null,
                    "liam",
                    "ramirez",
                    "liam.ramirez@email.com",
                    passwordEncoder.encode("lr.123"),
                    UserRole.ADMIN,
                    LocalDateTime.now(),
                    "liam");

            userRepository.saveAll(List.of(u1));
            userRepository.saveAll(List.of(u9));
            userRepository.saveAll(List.of(u8));
            userRepository.saveAll(List.of(u7));
            userRepository.saveAll(List.of(u6));
            userRepository.saveAll(List.of(u5));
            System.out.println("🟢 Initial users and roles inserted.");
        }

        if(propertyRepository.count() == 0 && propertyImageRepository.count()==0) {

            List<User> agents = userRepository.findByRole(UserRole.AGENT);
            if (agents.isEmpty()) {
                System.out.println("⚠️ No AGENT users found. Skipping property import.");
                return;
            }

            Random random = new Random();

            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/homedata.csv"))) {
                String line;
                boolean skipHeader = true;

                while ((line = br.readLine()) != null) {
                    if (skipHeader) {
                        skipHeader = false;
                        continue;
                    }

                    String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    if (fields.length < 5) continue; // Skip invalid lines

                    String title = fields[0].trim().replaceAll("^\"|\"$", "");
                    double price = Double.parseDouble(fields[1].trim());
                    String location =fields[2].trim().replace("\"", "");
                    int size = Integer.parseInt(fields[3].trim());
                    String description = fields[4].trim();


                    User randomAgent = agents.get(random.nextInt(agents.size()));
                    Property property = new Property(null, title, price, description, location, size, randomAgent);
                    propertyRepository.save(property);

                    // Handle images
                    String safeTitle = title.replaceAll("[\"\\\\/:*?<>|]", "").trim();
                    Path imageDir = Paths.get("src/main/resources/static/images/PropertyImages", safeTitle);
                    if (Files.exists(imageDir) && Files.isDirectory(imageDir)) {
                        Files.list(imageDir).forEach(imagePath -> {
                            String fileName = imagePath.getFileName().toString();
                            PropertyImage image = new PropertyImage(null, fileName, property);
                            propertyImageRepository.save(image);
                        });
                    }
                }

                System.out.println("🏡 Properties and images imported successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }



        } else {
            System.out.println("🟡 Users and roles already exist, skipping initialization.");
        }
    }
}
