package com.example.ktra_gki_ltdd.dto;

public class LoginResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String message;

    public LoginResponse() {}

    public LoginResponse(Long userId, String fullName, String email, String message) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
