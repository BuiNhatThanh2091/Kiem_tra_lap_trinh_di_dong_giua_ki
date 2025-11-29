package com.example.kiem_tra_giua_ki_di_dong.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // QUAN TRỌNG: Thay số IP này bằng IPv4 của máy tính bạn (xem bằng cmd -> ipconfig)
    // Ví dụ: 192.168.1.15. Đừng dùng localhost hay 10.0.2.2 nếu nó hay lỗi.
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}