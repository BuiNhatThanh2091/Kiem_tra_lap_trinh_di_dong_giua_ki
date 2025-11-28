package com.example.kiem_tra_giua_ki_di_dong.remote;

import com.example.kiem_tra_giua_ki_di_dong.model.ApiMessage;
import com.example.kiem_tra_giua_ki_di_dong.model.Category;
import com.example.kiem_tra_giua_ki_di_dong.model.LoginRequest;
import com.example.kiem_tra_giua_ki_di_dong.model.LoginResponse;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;
import com.example.kiem_tra_giua_ki_di_dong.model.RegisterRequest;
import com.example.kiem_tra_giua_ki_di_dong.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // 1. Lấy tất cả category món ăn
    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    // 2. Lấy danh sách sản phẩm theo category
    @GET("api/products/by-category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    // 3. Đăng ký
    @POST("api/auth/register")
    Call<ApiMessage> register(@Body RegisterRequest request);

    // 4. Đăng nhập
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 5. Đăng xuất
    @POST("api/auth/logout")
    Call<ApiMessage> logout();

    // 6. Lấy profile user theo userId
    @GET("api/profile/{userId}")
    Call<User> getProfile(@Path("userId") int userId);
}
