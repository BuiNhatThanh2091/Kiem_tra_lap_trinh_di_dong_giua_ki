package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var showEmail: TextView
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText
    private lateinit var verifyBtn : Button
    private lateinit var resend : TextView
    private var currentFlow: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otpverification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        otp6 = findViewById(R.id.otp6)
        verifyBtn = findViewById(R.id.verifyButton)
        showEmail = findViewById(R.id.showEmail)
        resend = findViewById(R.id.tvResend)

        val email = intent.getStringExtra("email") ?: "demoNhom7Baitap01@student.hcmute.edu.vn"
        currentFlow = intent.getStringExtra(OtpFlow.FLOW_TYPE) ?: "FORGOT_PASSWORD"

        showEmail.text = email

        verifyBtn.setOnClickListener {
            val enteredOtp = otp1.text.toString() +
                    otp2.text.toString() +
                    otp3.text.toString() +
                    otp4.text.toString() +
                    otp5.text.toString() +
                    otp6.text.toString()

            val correctOtp = "123456"

            if (enteredOtp.length == correctOtp.length && enteredOtp == correctOtp) {
                handleSuccessfulVerification()
            } else {
                Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_SHORT).show()
            }
        }

        resend.setOnClickListener {
            Toast.makeText(this, "OTP resent successfully!", Toast.LENGTH_SHORT).show()
        }

        otp1.doOnTextChanged { text, start, before, count ->
            if (text?.length == 1) {
                otp2.requestFocus()
            }
        }
        otp2.doOnTextChanged { text, start, before, count ->
            if (text?.length == 1) {
                otp3.requestFocus()
            } else if (text?.isEmpty() == true && before == 1) {
                otp1.requestFocus()
            }
        }
        otp3.doOnTextChanged { text, start, before, count ->
            if (text?.length == 1) {
                otp4.requestFocus()
            } else if (text?.isEmpty() == true && before == 1) {
                otp2.requestFocus()
            }
        }
        otp4.doOnTextChanged { text, start, before, count ->
            if (text?.length == 1) {
                otp5.requestFocus()
            } else if (text?.isEmpty() == true && before == 1) {
                otp3.requestFocus()
            }
        }
        otp5.doOnTextChanged { text, start, before, count ->
            if (text?.length == 1) {
                otp6.requestFocus()
            } else if (text?.isEmpty() == true && before == 1) {
                otp4.requestFocus()
            }
        }
        otp6.doOnTextChanged { text, start, before, count ->
            if (text?.length == 1) {
                otp6.clearFocus()
            } else if (text?.isEmpty() == true && before == 1) {
                otp5.requestFocus()
            }
        }
    }
    private fun handleSuccessfulVerification() {
        Toast.makeText(this, "OTP verified successfully!", Toast.LENGTH_SHORT).show()
        when (currentFlow) {
            OtpFlow.REGISTER -> {
                // khúc này chuyển tới màn hình login nè
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            OtpFlow.FORGOT_PASSWORD -> {
                // Chuyển tới màn hình đặt lại mật khẩu
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("email", intent.getStringExtra("email"))
                startActivity(intent)
                finish()
            }
        }
    }
}