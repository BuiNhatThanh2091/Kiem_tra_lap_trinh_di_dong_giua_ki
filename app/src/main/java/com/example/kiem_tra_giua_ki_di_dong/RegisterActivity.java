package com.example.kiem_tra_giua_ki_di_dong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputEditText etPhone;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btn_register);

        // Back to login button
        findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (validateInput(fullName, email, password, confirmPassword, phone)) {
                    // Send OTP to phone
                    Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateInput(String fullName, String email, String password, String confirmPassword, String phone) {
        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return false;
        }
        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (phone.length() < 10) {
            etPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }
        return true;
    }
}