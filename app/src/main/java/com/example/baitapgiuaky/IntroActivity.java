package com.example.baitapgiuaky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    
    private Button btnStart;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        btnStart = findViewById(R.id.btnStart);
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }
    
    private void setupClickListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStartButtonClick();
            }
        });
    }
    
    private void handleStartButtonClick() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        boolean hasAccount = sharedPreferences.getBoolean("hasAccount", false);
        
        Intent intent;
        
        if (isLoggedIn) {
            // Đã đăng nhập -> chuyển đến MainActivity
            intent = new Intent(IntroActivity.this, MainActivity.class);
        } else if (hasAccount) {
            // Có tài khoản nhưng chưa đăng nhập -> chuyển đến LoginActivity
            intent = new Intent(IntroActivity.this, LoginActivity.class);
        } else {
            // Chưa có tài khoản -> chuyển đến RegisterActivity
            intent = new Intent(IntroActivity.this, RegisterActivity.class);
        }
        
        startActivity(intent);
        finish(); // Đóng IntroActivity
    }
}