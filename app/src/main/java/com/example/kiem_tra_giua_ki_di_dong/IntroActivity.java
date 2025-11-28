package com.example.kiem_tra_giua_ki_di_dong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btnStart = findViewById(R.id.btn_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is already logged in
                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(IntroActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }
}