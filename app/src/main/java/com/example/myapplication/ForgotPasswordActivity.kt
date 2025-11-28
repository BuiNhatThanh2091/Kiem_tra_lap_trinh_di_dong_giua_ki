package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var btnSendOTP: Button
    private lateinit var btnBackToLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        btnSendOTP = findViewById(R.id.btnSendOTP)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)
    }

    private fun setupClickListeners() {
        btnSendOTP.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (validateEmail(email)) {
                sendOTPToEmail(email)
            }
        }

        btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun sendOTPToEmail(email: String) {
        Toast.makeText(this, "OTP has been sent to: $email", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, OTPVerificationActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra(OtpFlow.FLOW_TYPE, OtpFlow.FORGOT_PASSWORD)
        startActivity(intent)
    }
}
