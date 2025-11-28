package com.example.kiem_tra_giua_ki_di_dong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kiem_tra_giua_ki_di_dong.api.RetrofitClient;
import com.example.kiem_tra_giua_ki_di_dong.model.RegisterMessage;
import com.example.kiem_tra_giua_ki_di_dong.model.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFullName, edtPass, edtEmail;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtUsername); // XML id cũ, dùng cho FullName
        edtFullName.setHint("Họ và tên");

        edtPass = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = edtFullName.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();

            if (name.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            register(name, email, pass);
        });
    }

    private void register(String name, String email, String pass) {
        RegisterRequest request = new RegisterRequest(name, email, pass);
        RetrofitClient.getApiService().registerUser(request).enqueue(new Callback<RegisterMessage>() {
            @Override
            public void onResponse(Call<RegisterMessage> call, Response<RegisterMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Check mail OTP nhé!", Toast.LENGTH_LONG).show();
                        // Chuyển sang nhập OTP
                        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                        intent.putExtra("EMAIL_KEY", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi Server hoặc Email trùng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RegisterMessage> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}