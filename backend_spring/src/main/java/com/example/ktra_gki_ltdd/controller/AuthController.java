package com.example.ktra_gki_ltdd.controller;

import com.example.ktra_gki_ltdd.dto.ApiMessage;
import com.example.ktra_gki_ltdd.dto.LoginRequest;
import com.example.ktra_gki_ltdd.dto.LoginResponse;
import com.example.ktra_gki_ltdd.dto.RegisterRequest;
import com.example.ktra_gki_ltdd.entity.User;
import com.example.ktra_gki_ltdd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Bộ nhớ tạm (RAM)
    private Map<String, RegisterData> tempStorage = new ConcurrentHashMap<>();

    // Class nội bộ để lưu dữ liệu tạm
    private static class RegisterData {
        RegisterRequest request;
        String otp;

        public RegisterData(RegisterRequest request, String otp) {
            this.request = request;
            this.otp = otp;
        }
    }

    // 1. ĐĂNG KÝ (Lưu RAM, Gửi OTP)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiMessage(false, "Email đã tồn tại trong hệ thống"));
        }

        String otp = generateOtp();

        // Lưu tạm vào RAM
        tempStorage.put(request.getEmail(), new RegisterData(request, otp));

        // Gửi mail
        new Thread(() -> sendEmail(request.getEmail(), otp)).start();

        return ResponseEntity.ok(new ApiMessage(true, "Mã OTP đã gửi. Vui lòng kiểm tra email."));
    }

    // 2. XÁC THỰC OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (!tempStorage.containsKey(email)) {
            return ResponseEntity.badRequest().body(new ApiMessage(false, "Yêu cầu đăng ký đã hết hạn hoặc không tồn tại"));
        }

        RegisterData data = tempStorage.get(email);

        if (data.otp.equals(otp)) {
            // Lưu vào Database
            User user = new User();
            user.setFullName(data.request.getFullName());
            user.setEmail(data.request.getEmail());
            user.setPassword(data.request.getPassword());
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            // Xóa khỏi RAM
            tempStorage.remove(email);

            return ResponseEntity.ok(new ApiMessage(true, "Kích hoạt và Đăng ký thành công!"));
        } else {
            return ResponseEntity.badRequest().body(new ApiMessage(false, "Mã OTP không đúng"));
        }
    }

    // --- 3. MỚI THÊM: GỬI LẠI OTP (RESEND) ---
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email) {
        // Kiểm tra xem có dữ liệu trong RAM không
        if (!tempStorage.containsKey(email)) {
            return ResponseEntity.badRequest().body(new ApiMessage(false, "Không tìm thấy yêu cầu đăng ký. Vui lòng đăng ký lại."));
        }

        // Lấy dữ liệu cũ
        RegisterData oldData = tempStorage.get(email);

        // Sinh OTP mới
        String newOtp = generateOtp();

        // Cập nhật lại OTP mới vào RAM (Giữ nguyên thông tin user cũ)
        oldData.otp = newOtp;
        tempStorage.put(email, oldData);

        // Gửi lại mail
        new Thread(() -> sendEmail(email, newOtp)).start();

        return ResponseEntity.ok(new ApiMessage(true, "Đã gửi lại mã OTP mới."));
    }

    // Hàm phụ: Sinh OTP
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // Hàm phụ: Gửi Mail
    private void sendEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Mã OTP xác thực tài khoản (Gửi mới)");
            message.setText("Mã OTP của bạn là: " + otp + "\nMã này dùng để kích hoạt tài khoản.");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 3. ĐĂNG NHẬP
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // findByEmail trả về Optional<User>
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            // Sai email
            LoginResponse res = new LoginResponse();
            res.setUserId(0L);          // dùng 0L vì userId là Long
            res.setFullName(null);
            res.setEmail(null);
            res.setMessage("Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        User user = optionalUser.get();

        // So sánh mật khẩu (hiện đang lưu plain-text 123456)
        if (!user.getPassword().equals(request.getPassword())) {
            LoginResponse res = new LoginResponse();
            res.setUserId(0L);          // cũng dùng 0L
            res.setFullName(null);
            res.setEmail(null);
            res.setMessage("Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        // Đăng nhập thành công
        LoginResponse res = new LoginResponse();
        res.setUserId(user.getId());    // getId() là Long thì OK
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setMessage("Đăng nhập thành công");

        return ResponseEntity.ok(res);
    }


}