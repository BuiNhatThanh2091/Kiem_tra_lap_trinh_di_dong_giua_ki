package com.example.kiem_tra_giua_ki_di_dong.api;

import com.example.kiem_tra_giua_ki_di_dong.model.RegisterMessage;
import com.example.kiem_tra_giua_ki_di_dong.model.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    // API 1: Đăng ký
    @POST("api/auth/register")
    Call<RegisterMessage> registerUser(@Body RegisterRequest request);

    // API 2: Xác thực OTP (Dùng @Query vì backend dùng @RequestParam)
    @POST("api/auth/verify-otp")
    Call<RegisterMessage> verifyOtp(@Query("email") String email, @Query("otp") String otp);
}