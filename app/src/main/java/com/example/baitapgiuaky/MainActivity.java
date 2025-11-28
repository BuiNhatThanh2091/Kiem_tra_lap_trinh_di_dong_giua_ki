package com.example.baitapgiuaky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvWelcome;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;
    
    // Bottom navigation buttons
    private LinearLayout homeBtn, profileBtn, centerBtn, supportBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupClickListeners();
        loadUserInfo();
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        
        // Initialize bottom navigation buttons
        homeBtn = findViewById(R.id.homeBtn);
        profileBtn = findViewById(R.id.profileBtn);
        centerBtn = findViewById(R.id.centerBtn);
        supportBtn = findViewById(R.id.supportBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
    }
    
    private void setupClickListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogout();
            }
        });
        
        // Bottom navigation click listeners
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                // Navigate to home screen or refresh current content
            }
        });
        
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                // Navigate to profile screen
                showUserProfile();
            }
        });
        
        centerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Center button clicked", Toast.LENGTH_SHORT).show();
                // Navigate to center feature
            }
        });
        
        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Support clicked", Toast.LENGTH_SHORT).show();
                // Navigate to support screen
            }
        });
        
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                // Navigate to settings screen
            }
        });
    }
    
    private void loadUserInfo() {
        String username = sharedPreferences.getString("username", "Người dùng");
        tvWelcome.setText("Chào mừng, " + username + "!");
    }
    
    private void showUserProfile() {
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        
        String profileInfo = "Tên đăng nhập: " + username + "\nEmail: " + email;
        Toast.makeText(this, profileInfo, Toast.LENGTH_LONG).show();
    }
    
    private void performLogout() {
        // Xóa trạng thái đăng nhập
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        
        Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        
        // Quay về IntroActivity
        Intent intent = new Intent(MainActivity.this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}