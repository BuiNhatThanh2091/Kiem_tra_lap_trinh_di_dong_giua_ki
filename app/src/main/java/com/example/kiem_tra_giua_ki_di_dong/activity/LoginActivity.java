package com.example.kiem_tra_giua_ki_di_dong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.LoginRequest;
import com.example.kiem_tra_giua_ki_di_dong.model.LoginResponse;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiClient;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private ApiService apiService;   // dùng để gọi API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Nếu đã đăng nhập rồi thì vào thẳng Home
        boolean isLoggedIn = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Khởi tạo ApiService
        apiService = ApiClient.getApiService();

        // Xử lý nút Login
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty()) {
                etEmail.setError("Vui lòng nhập email");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            doLogin(email, password);
        });

        // Đi tới màn đăng ký
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void doLogin(String email, String password) {
        // Tạo body gửi lên API
        LoginRequest request = new LoginRequest(email, password);

        // Gọi API
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Ở đây mình giả định: userId > 0 nghĩa là đăng nhập thành công
                    if (loginResponse.getUserId() > 0) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Lưu trạng thái đăng nhập
                        getSharedPreferences("UserSession", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isLoggedIn", true)
                                .putInt("userId", loginResponse.getUserId())
                                .putString("fullName", loginResponse.getFullName())
                                .putString("email", loginResponse.getEmail())
                                .apply();

                        // Điều hướng sang HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Nếu backend trả message lỗi
                        String msg = loginResponse.getMessage() != null
                                ? loginResponse.getMessage()
                                : "Email hoặc mật khẩu không đúng";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại! Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
