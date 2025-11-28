//<!--Dao Tuan Duy - 23162011-->
package com.example.kiem_tra_giua_ki_di_dong.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiem_tra_giua_ki_di_dong.R;

import java.util.ArrayList;
import java.util.List;

import com.example.kiem_tra_giua_ki_di_dong.adapter.CategoryAdapter;
import com.example.kiem_tra_giua_ki_di_dong.adapter.ProductAdapter;
import com.example.kiem_tra_giua_ki_di_dong.model.Category;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiService;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsByCategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private ProgressBar progressBar;
    private TextView tvNoMoreProducts;

    private List<Product> productList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<Product> allProductsCache = new ArrayList<>(); // Cache tất cả products

    private int currentCategoryId = -1;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    private static final int ITEMS_PER_PAGE = 6; // Số sản phẩm mỗi lần load

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products_by_category);

        initViews();
        setupRecyclerView();
        loadCategories();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        progressBar = findViewById(R.id.progressBar);
        tvNoMoreProducts = findViewById(R.id.tvNoMoreProducts);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(productAdapter);

        // Lazy loading - detect scroll to bottom
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Chỉ load khi scroll xuống (dy > 0)
                if (dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                        int visibleItemCount = layoutManager.getChildCount();

                        // Load more khi còn 2 items nữa là đến cuối
                        if (!isLoading && hasMorePages &&
                                (lastVisibleItem + 2) >= totalItemCount &&
                                visibleItemCount > 0) {

                            currentPage++;
                            android.util.Log.d("SCROLL", "Loading page: " + currentPage);
                            loadProducts(currentCategoryId, currentPage, false);
                        }
                    }
                }
            }
        });
    }

    private void loadCategories() {
        // Hiển thị loading
        progressBar.setVisibility(View.VISIBLE);

//        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        ApiService apiService = ApiClient.getApiService();


        Call<List<Category>> call = apiService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();

                    // Log để debug
                    android.util.Log.d("API_DEBUG", "Categories loaded: " + categoryList.size());

                    displayCategories();

                    if (!categoryList.isEmpty()) {
                        currentCategoryId = categoryList.get(0).getId();
                        loadProducts(currentCategoryId, 1, true);
                    } else {
                        Toast.makeText(ProductsByCategoryActivity.this,
                                "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductsByCategoryActivity.this,
                            "Lỗi tải danh mục - Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    android.util.Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductsByCategoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("API_ERROR", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void displayCategories() {
        // Setup RecyclerView cho categories
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerViewCategories.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(this, categoryList, (category, position) -> {
            // Reset trước khi load category mới
            currentCategoryId = category.getId();
            currentPage = 1;
            hasMorePages = true;
            allProductsCache.clear();

            loadProducts(currentCategoryId, currentPage, true);
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

        // Auto select first category
        if (!categoryList.isEmpty()) {
            categoryAdapter.setSelectedPosition(0);
        }
    }

    private void loadProducts(int categoryId, int page, boolean clearList) {
        if (isLoading) return;

        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        tvNoMoreProducts.setVisibility(View.GONE);

        // Nếu đổi category hoặc load lần đầu
        if (clearList || currentCategoryId != categoryId) {
            // Reset và load tất cả products từ API
//            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            ApiService apiService = ApiClient.getApiService();

            Call<List<Product>> call = apiService.getProductsByCategory(categoryId);

            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        // Lưu tất cả products vào cache
                        allProductsCache = new ArrayList<>(response.body());

                        // Sắp xếp theo giá tăng dần
                        allProductsCache.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));

                        // Clear list hiện tại và notify
                        int oldSize = productList.size();
                        productList.clear();
                        if (oldSize > 0) {
                            productAdapter.notifyItemRangeRemoved(0, oldSize);
                        }

                        // Reset page về 1
                        currentPage = 1;
                        hasMorePages = true;

                        // Load trang đầu tiên
                        loadPageFromCache(1);

                    } else {
                        Toast.makeText(ProductsByCategoryActivity.this,
                                "Lỗi tải sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductsByCategoryActivity.this,
                            "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Load trang tiếp theo từ cache
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            loadPageFromCache(page);
        }
    }

    private void loadPageFromCache(int page) {
        if (allProductsCache.isEmpty()) {
            tvNoMoreProducts.setText("Không có sản phẩm");
            tvNoMoreProducts.setVisibility(View.VISIBLE);
            hasMorePages = false;
            return;
        }

        // Tính toán index
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allProductsCache.size());

        // Kiểm tra còn data không
        if (startIndex >= allProductsCache.size()) {
            hasMorePages = false;
            tvNoMoreProducts.setText("Đã hiển thị tất cả sản phẩm");
            tvNoMoreProducts.setVisibility(View.VISIBLE);
            return;
        }

        // Lấy products cho trang này
        List<Product> pageProducts = new ArrayList<>(allProductsCache.subList(startIndex, endIndex));

        // SỬ DỤNG notifyItemRangeInserted thay vì notifyDataSetChanged
        int oldSize = productList.size();
        productList.addAll(pageProducts);
        productAdapter.notifyItemRangeInserted(oldSize, pageProducts.size());

        // Kiểm tra còn trang tiếp theo không
        if (endIndex >= allProductsCache.size()) {
            hasMorePages = false;
            tvNoMoreProducts.setText("Đã hiển thị tất cả sản phẩm");
            tvNoMoreProducts.setVisibility(View.VISIBLE);
        } else {
            hasMorePages = true;
            tvNoMoreProducts.setVisibility(View.GONE);
        }

        // Log để debug
        android.util.Log.d("PAGINATION", "Page: " + page +
                ", Loaded: " + pageProducts.size() +
                ", Total displayed: " + productList.size() +
                ", Has more: " + hasMorePages);
    }
}