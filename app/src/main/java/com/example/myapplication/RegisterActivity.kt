package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var registerBtn: Button
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEt = findViewById(R.id.Username)
        emailEt = findViewById(R.id.EmailAddress)
        passwordEt = findViewById(R.id.Password)
        registerBtn = findViewById(R.id.regbutton)
        loginBtn = findViewById(R.id.buttonLogin)

        registerBtn.setOnClickListener { attemptRegister() }
        loginBtn.setOnClickListener { navigateToLogin() }
    }

    private fun attemptRegister() {
        val username = usernameEt.text.toString().trim()
        val email = emailEt.text.toString().trim()
        val password = passwordEt.text.toString()

        when {
            username.isEmpty() -> toast("Username required")
            email.isEmpty() -> toast("Email required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> toast("Invalid email")
            password.length < 6 -> toast("Password >= 6 chars")
            else -> {
                toast("Registered: $username")
                val intent = Intent(this, OTPVerificationActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra(OtpFlow.FLOW_TYPE, OtpFlow.REGISTER)
                startActivity(intent)
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}
