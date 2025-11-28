package com.example.ktra_gki_ltdd.dto;

public class ApiMessage {

    private boolean success;
    private String message;

    public ApiMessage() {}

    public ApiMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
