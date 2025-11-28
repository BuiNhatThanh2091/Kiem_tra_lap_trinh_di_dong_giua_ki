package com.example.kiem_tra_giua_ki_di_dong.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiem_tra_giua_ki_di_dong.R;


public class MainActivity extends AppCompatActivity {

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
        
        TextView tvGreeting = findViewById(R.id.tv_greeting);
        TextView tvAppName = findViewById(R.id.tv_app_name);
        ImageView imgUser = findViewById(R.id.img_user);

        String userName = "Trung";
        tvGreeting.setText(getString(R.string.welcome, userName));
        tvAppName.setText(R.string.eat_and_order);
        imgUser.setImageResource(R.drawable.pro_trung);
    }
}
