package com.example.kiem_tra_giua_ki_di_dong;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private TextView tvResend, tvTimer, tvPhoneNumber;
    private String fullName, email, password, phone;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Get data from RegisterActivity
        fullName = getIntent().getStringExtra("fullName");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        phone = getIntent().getStringExtra("phone");

        initViews();
        setupListeners();
        startTimer();

        // Display masked phone number
        if (phone != null && phone.length() > 6) {
            String maskedPhone = phone.substring(0, 3) + "***" + phone.substring(phone.length() - 3);
            tvPhoneNumber.setText("Mã OTP đã được gửi đến " + maskedPhone);
        } else {
            tvPhoneNumber.setText("Mã OTP đã được gửi đến số điện thoại của bạn");
        }
    }

    private void initViews() {
        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvTimer = findViewById(R.id.tvTimer);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getEnteredOtp();
                if (otp.length() == 6) {
                    verifyOtp(otp);
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Vui lòng nhập đầy đủ mã OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    resendOtp();
                    startTimer();
                }
            }
        });
    }

    private String getEnteredOtp() {
        return etOtp.getText().toString().trim();
    }

    private void verifyOtp(String otp) {
        // For demo purposes, accept "123456" as valid OTP
        if ("123456".equals(otp)) {
            Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
            
            // TODO: Call API to register user
            // registerUser();
            
            // Navigate to login screen
            Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
            clearOtpInputs();
        }
    }

    private void clearOtpInputs() {
        etOtp.setText("");
        etOtp.requestFocus();
    }

    private void resendOtp() {
        // Simulate resending OTP
        Toast.makeText(this, "Đã gửi lại mã. OTP của bạn là: 123456", Toast.LENGTH_LONG).show();
    }

    private void startTimer() {
        isTimerRunning = true;
        tvResend.setVisibility(View.GONE);
        tvTimer.setVisibility(View.VISIBLE);
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Gửi lại sau " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
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
