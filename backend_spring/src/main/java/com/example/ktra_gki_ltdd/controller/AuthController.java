package com.example.ktra_gki_ltdd.controller;

import com.example.ktra_gki_ltdd.dto.ApiMessage;
import com.example.ktra_gki_ltdd.dto.LoginRequest;
import com.example.ktra_gki_ltdd.dto.LoginResponse;
import com.example.ktra_gki_ltdd.dto.RegisterRequest;
import com.example.ktra_gki_ltdd.entity.User;
import com.example.ktra_gki_ltdd.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessage(false, "Email đã tồn tại"));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        // nếu đề không yêu cầu mã hoá thì lưu plain cho đơn giản
        user.setPassword(request.getPassword());
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok(new ApiMessage(true, "Đăng ký thành công"));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    if (!user.getPassword().equals(request.getPassword())) {
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiMessage(false, "Sai mật khẩu"));
                    }

                    LoginResponse response = new LoginResponse(
                            user.getId(),
                            user.getFullName(),
                            user.getEmail(),
                            "Đăng nhập thành công"
                    );

                    return ResponseEntity.ok(response);
                })
                .orElse(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiMessage(false, "Email không tồn tại"))
                );
    }
}
