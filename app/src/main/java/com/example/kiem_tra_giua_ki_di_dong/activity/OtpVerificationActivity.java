package com.example.kiem_tra_giua_ki_di_dong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.ApiMessage;
import com.example.kiem_tra_giua_ki_di_dong.remote.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextInputEditText etOtp;
    private Button btnVerify;
    private TextView tvResend, tvTimer, tvInfo;
    private String emailUser;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        emailUser = getIntent().getStringExtra("email");

        initViews();
        setupListeners();
        startTimer();

        if (emailUser != null) {
            tvInfo.setText("Mã OTP đã được gửi đến email: " + emailUser);
        }
    }

    private void initViews() {
        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvTimer = findViewById(R.id.tvTimer);
        tvInfo = findViewById(R.id.tvPhoneNumber);
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(v -> {
            String otp = getEnteredOtp();
            if (otp.length() == 6) {
                verifyOtpOnServer(otp);
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            if (!isTimerRunning) {
                resendOtpToServer();
            }
        });
    }

    private void resendOtpToServer() {
        Toast.makeText(this, "Đang gửi lại...", Toast.LENGTH_SHORT).show();
        RetrofitClient.getApiService().resendOtp(emailUser).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().isSuccess()) {
                        Toast.makeText(OtpVerificationActivity.this, "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show();
                        startTimer();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Không thể gửi lại OTP.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getEnteredOtp() {
        if (etOtp.getText() != null) {
            return etOtp.getText().toString().trim();
        }
        return "";
    }


    private void verifyOtpOnServer(String otpInput) {
        RetrofitClient.getApiService().verifyOtp(emailUser, otpInput).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(OtpVerificationActivity.this, "Kích hoạt thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        clearOtpInputs();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearOtpInputs() {
        etOtp.setText("");
        etOtp.requestFocus();
    }

    private void startTimer() {
        tvTimer.setVisibility(View.VISIBLE);
        tvResend.setVisibility(View.GONE);
        isTimerRunning = true;

        countDownTimer = new CountDownTimer(30000, 1000) { // 30 giây
            @Override public void onTick(long millisUntilFinished) {
                tvTimer.setText("Gửi lại mã sau 00:" + (millisUntilFinished / 1000));
            }
            @Override public void onFinish() {
                isTimerRunning = false;
                tvTimer.setVisibility(View.GONE);
                tvResend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}