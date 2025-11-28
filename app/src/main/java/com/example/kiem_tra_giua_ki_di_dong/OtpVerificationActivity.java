package com.example.kiem_tra_giua_ki_di_dong;

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

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
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
        setupOtpInputs();
        setupListeners();
        startTimer();

        // Display masked phone number
        String maskedPhone = phone.substring(0, 3) + "***" + phone.substring(phone.length() - 3);
        tvPhoneNumber.setText("Mã OTP đã được gửi đến " + maskedPhone);
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
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
    }

    private void setupOtpInputs() {
        EditText[] otpInputs = {etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6};
        
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
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
        return etOtp1.getText().toString() +
               etOtp2.getText().toString() +
               etOtp3.getText().toString() +
               etOtp4.getText().toString() +
               etOtp5.getText().toString() +
               etOtp6.getText().toString();
    }

    private void verifyOtp(String otp) {
        // For demo purposes, accept "123456" as valid OTP
        // In real implementation, call API to verify OTP
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
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");
        etOtp5.setText("");
        etOtp6.setText("");
        etOtp1.requestFocus();
    }

    private void resendOtp() {
        // TODO: Call API to resend OTP
        Toast.makeText(this, "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        isTimerRunning = true;
        tvResend.setEnabled(false);
        tvResend.setTextColor(getColor(android.R.color.darker_gray));
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Gửi lại sau " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                tvTimer.setText("");
                tvResend.setEnabled(true);
                tvResend.setTextColor(getColor(R.color.primary_color));
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