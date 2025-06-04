package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.utils.CurrentUserContext;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public void prepareSettingsModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        User user = context.user();
        Authentication auth = context.auth();

        model.addAttribute("user", user);

        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

//        if (isManager) {
//            List<User> currentEmployees = userRepository.findByManager(user);
//            List<User> availableUsers = userRepository.findByManagerIsNull().stream()
//                    .filter(u -> !u.getEmail().equals(user.getEmail()))
//                    .collect(Collectors.toList());
//
//            model.addAttribute("currentEmployees", currentEmployees);
//            model.addAttribute("availableUsers", availableUsers);
//        }
    }

    @Override
    public void updateUserSettings(User updatedUser, String password, List<Long> addIds, List<Long> removeIds) {
        User user = getCurrentUserContext().user();

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

//        if (addIds != null) {
//            for (Long empId : addIds) {
//                Users emp = userRepository.findById(empId).orElseThrow();
//                emp.setManager(user);
//                userRepository.save(emp);
//            }
//        }

//        if (removeIds != null) {
//            for (Long empId : removeIds) {
//                Users emp = userRepository.findById(empId).orElseThrow();
//                emp.setManager(null);
//                userRepository.save(emp);
//            }
//        }

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
