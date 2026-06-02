package com.karuna.controller;

import com.karuna.entity.User;
import com.karuna.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/responders")
    public ResponseEntity<List<User>> getResponders() {
        // Return both NGO and VOLUNTEER users
        List<User> responders = userRepo.findAll().stream()
                .filter(u -> u.getRole() == User.Role.VOLUNTEER || u.getRole() == User.Role.NGO)
                .toList();
        return ResponseEntity.ok(responders);
    }
}
