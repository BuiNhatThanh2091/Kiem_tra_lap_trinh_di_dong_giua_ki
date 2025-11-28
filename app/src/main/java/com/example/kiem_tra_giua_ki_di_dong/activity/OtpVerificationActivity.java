package com.example.kiem_tra_giua_ki_di_dong.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.ApiMessage;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private Button btnVerify;
    private TextView tvResend, tvTimer, tvInfo;
    private String emailUser; // Quan trọng: Email để gửi lên server xác thực
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Lấy email từ màn hình trước
        emailUser = getIntent().getStringExtra("email");
        String phoneDisplay = getIntent().getStringExtra("phone");

        initViews();
        setupOtpInputs();
        setupListeners();
        startTimer();

        if (emailUser != null) {
            tvInfo.setText("Mã OTP đã được gửi đến email: " + emailUser);
        }
    }

    private void initViews() {
        etOtp1 = findViewById(R.id.et_otp_1);
        etOtp2 = findViewById(R.id.et_otp_2);
        etOtp3 = findViewById(R.id.et_otp_3);
        etOtp4 = findViewById(R.id.et_otp_4);
        etOtp5 = findViewById(R.id.et_otp_5);
        etOtp6 = findViewById(R.id.et_otp_6);
        btnVerify = findViewById(R.id.btn_verify);
        tvResend = findViewById(R.id.tv_resend);
        tvTimer = findViewById(R.id.tv_timer);
        tvInfo = findViewById(R.id.tv_phone_number); // Dùng lại ID cũ nhưng hiển thị Email
    }

    private void setupOtpInputs() {
        EditText[] otpInputs = {etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6};
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(v -> {
            String otp = getEnteredOtp();
            if (otp.length() == 6) {
                verifyOtpOnServer(otp);
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // TODO: Gọi API gửi lại OTP nếu cần (Hiện tại dùng lại API Register cũng được)
                Toast.makeText(this, "Đang gửi lại...", Toast.LENGTH_SHORT).show();
                startTimer();
            }
        });
    }

    private String getEnteredOtp() {
        return etOtp1.getText().toString() + etOtp2.getText().toString() +
                etOtp3.getText().toString() + etOtp4.getText().toString() +
                etOtp5.getText().toString() + etOtp6.getText().toString();
    }

    private void verifyOtpOnServer(String otpInput) {
        // GỌI API XÁC THỰC
        ApiClient.getApiService().verifyOtp(emailUser, otpInput).enqueue(new Callback<ApiMessage>() {
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
        etOtp1.setText(""); etOtp2.setText(""); etOtp3.setText("");
        etOtp4.setText(""); etOtp5.setText(""); etOtp6.setText("");
        etOtp1.requestFocus();
    }

    private void startTimer() {
        isTimerRunning = true;
        tvResend.setEnabled(false);
        tvResend.setTextColor(getColor(android.R.color.darker_gray));

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                tvTimer.setText("Gửi lại sau " + millisUntilFinished / 1000 + "s");
            }
            @Override public void onFinish() {
                isTimerRunning = false;
                tvTimer.setText("");
                tvResend.setEnabled(true);
                tvResend.setTextColor(getColor(R.color.purple_500)); // Sửa lại màu cho chuẩn
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}