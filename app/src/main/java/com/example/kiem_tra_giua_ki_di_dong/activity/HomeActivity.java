//<!--Dao Tuan Duy - 23162011-->
package com.example.kiem_tra_giua_ki_di_dong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.adapter.CategoryAdapter;
import com.example.kiem_tra_giua_ki_di_dong.adapter.ProductHorizontalAdapter;
import com.example.kiem_tra_giua_ki_di_dong.model.Category;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiService;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private RecyclerView rvLastProducts;
    private CategoryAdapter categoryAdapter;
    private ProductHorizontalAdapter productAdapter;
    private ApiService apiService;
    private List<Category> categories = new ArrayList<>();
    private List<Product> lastProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_coordinator), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerViews();
        initApiService();
        loadData();
    }

    private void initViews() {
        TextView tvGreeting = findViewById(R.id.tv_greeting);
        TextView tvAppName = findViewById(R.id.tv_app_name);
        ImageView imgUser = findViewById(R.id.img_user);
        rvCategories = findViewById(R.id.rv_categories);
        rvLastProducts = findViewById(R.id.rv_last_products);

        String userName = "Trung"; // sau này có thể lấy từ SharedPreferences
        tvGreeting.setText(getString(R.string.welcome, userName));
        tvAppName.setText(R.string.eat_and_order);
        imgUser.setImageResource(R.drawable.pro_trung);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.bottom_item_settings).setOnClickListener(v -> showLogoutDialog());

        findViewById(R.id.bottom_item_home).setOnClickListener(v -> {
            // Đang ở Home rồi, có thể scroll lên đầu nếu muốn
        });

        findViewById(R.id.bottom_item_profile).setOnClickListener(v ->
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.bottom_item_support).setOnClickListener(v ->
                Toast.makeText(this, "Support", Toast.LENGTH_SHORT).show()
        );
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Clear login state
                    getSharedPreferences("UserSession", MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupRecyclerViews() {
        // ====== Categories horizontal ======
        LinearLayoutManager categoryLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(categoryLayoutManager);

        // Truyền listener đúng với interface trong CategoryAdapter
        categoryAdapter = new CategoryAdapter(
                this,
                categories,
                (category, position) -> {
                    // Khi click vào category -> mở màn danh sách sản phẩm theo category
                    Intent intent = new Intent(HomeActivity.this, ProductsByCategoryActivity.class);
                    intent.putExtra("categoryId", category.getId());
                    intent.putExtra("categoryName", category.getName());
                    startActivity(intent);
                }
        );
        rvCategories.setAdapter(categoryAdapter);

        // ====== Last products horizontal ======
        LinearLayoutManager productLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvLastProducts.setLayoutManager(productLayoutManager);

        productAdapter = new ProductHorizontalAdapter(this, lastProducts);
        rvLastProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(product ->
                Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show()
        );
    }

    private void initApiService() {
        apiService = ApiClient.getApiService();
    }

    private void loadData() {
        loadCategories();
    }

    private void loadCategories() {
        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();

                    // Sau khi có category thì load sản phẩm của category đầu tiên
                    loadLastProducts();
                } else {
                    showError("Không thể tải danh mục");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void loadLastProducts() {
        if (!categories.isEmpty()) {
            int firstCategoryId = categories.get(0).getId();
            apiService.getProductsByCategory(firstCategoryId).enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        lastProducts.clear();
                        List<Product> products = response.body();

                        int maxProducts = Math.min(5, products.size());
                        for (int i = 0; i < maxProducts; i++) {
                            lastProducts.add(products.get(i));
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    showError("Lỗi tải sản phẩm: " + t.getMessage());
                }
            });
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
