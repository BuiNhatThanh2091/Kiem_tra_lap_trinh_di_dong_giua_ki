package com.example.ktra_gki_ltdd.controller;

import com.example.ktra_gki_ltdd.dto.UserProfileResponse;
import com.example.ktra_gki_ltdd.entity.User;
import com.example.ktra_gki_ltdd.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /api/profile/{userId}
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    UserProfileResponse profile = mapToProfile(user);
                    return ResponseEntity.ok(profile);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UserProfileResponse mapToProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
