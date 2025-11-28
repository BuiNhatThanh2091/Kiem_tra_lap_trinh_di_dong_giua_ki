package com.example.kiem_tra_giua_ki_di_dong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kiem_tra_giua_ki_di_dong.api.RetrofitClient;
import com.example.kiem_tra_giua_ki_di_dong.model.RegisterMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    EditText edtOtp;
    Button btnConfirm;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        edtOtp = findViewById(R.id.edtOtp);
        btnConfirm = findViewById(R.id.btnConfirmOtp);
        email = getIntent().getStringExtra("EMAIL_KEY");

        btnConfirm.setOnClickListener(v -> verify());
    }

    private void verify() {
        String otp = edtOtp.getText().toString().trim();
        if (otp.length() < 6) {
            Toast.makeText(this, "OTP phải đủ 6 số", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApiService().verifyOtp(email, otp).enqueue(new Callback<RegisterMessage>() {
            @Override
            public void onResponse(Call<RegisterMessage> call, Response<RegisterMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(OtpActivity.this, "Kích hoạt thành công!", Toast.LENGTH_SHORT).show();
                        // Về Login
                        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OtpActivity.this, "Sai OTP rồi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OtpActivity.this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RegisterMessage> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}