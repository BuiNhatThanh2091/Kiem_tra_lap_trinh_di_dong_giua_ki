package com.example.kiem_tra_giua_ki_di_dong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.ApiMessage;
import com.example.kiem_tra_giua_ki_di_dong.model.RegisterRequest;
import com.example.kiem_tra_giua_ki_di_dong.remote.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);

        findViewById(R.id.tvSignIn).setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (validateInput(fullName, email, password, confirmPassword, phone)) {
                // GỌI API ĐĂNG KÝ
                performRegister(fullName, email, password);
            }
        });
    }

    private void performRegister(String fullName, String email, String password) {
        RegisterRequest request = new RegisterRequest(fullName, email, password);

        RetrofitClient.getApiService().register(request).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Đã gửi mã OTP qua Email!", Toast.LENGTH_LONG).show();

                        // Chuyển sang màn hình nhập OTP
                        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("email", email); // Chỉ cần truyền Email để xác thực
                        // Có thể truyền thêm sđt nếu muốn hiển thị cho đẹp
                        intent.putExtra("phone", etPhone.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Email có thể đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String fullName, String email, String password, String confirmPassword, String phone) {
        if (fullName.isEmpty()) { etFullName.setError("Nhập họ tên"); return false; }
        if (email.isEmpty()) { etEmail.setError("Nhập email"); return false; }
        if (password.isEmpty() || password.length() < 6) { etPassword.setError("Mật khẩu >= 6 ký tự"); return false; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Mật khẩu không khớp"); return false; }
        if (phone.isEmpty()) { etPhone.setError("Nhập số điện thoại"); return false; }
        return true;
    }
}