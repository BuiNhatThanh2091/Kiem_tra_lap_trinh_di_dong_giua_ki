package com.example.kiem_tra_giua_ki_di_dong.model;
import com.google.gson.annotations.SerializedName;

public class RegisterMessage {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}