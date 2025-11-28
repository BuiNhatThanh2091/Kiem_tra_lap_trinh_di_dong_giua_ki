package com.example.kiem_tra_giua_ki_di_dong.model;
import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("fullName") // Khớp với Backend
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public RegisterRequest(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
}