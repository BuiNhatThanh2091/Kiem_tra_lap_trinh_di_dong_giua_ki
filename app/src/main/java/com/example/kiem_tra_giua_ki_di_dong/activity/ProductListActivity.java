package com.example.kiem_tra_giua_ki_di_dong.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.adapter.ProductGridAdapter;
import com.example.kiem_tra_giua_ki_di_dong.model.Category;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiService;
import com.example.kiem_tra_giua_ki_di_dong.remote.ApiClient;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private static final int ITEMS_PER_PAGE = 20;

    private Toolbar toolbar;
    private TextInputLayout tilCategory;
    private AutoCompleteTextView actvCategory;
    private TextView tvProductCount;
    private TextView tvSortInfo;
    private RecyclerView rvProducts;
    private View llEmptyState;
    private ProgressBar progressBar;

    private ProductGridAdapter adapter;
    private ApiService apiService;
    private List<Category> categories = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    private int currentCategoryId = -1;
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private int totalProductCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupCategoryDropdown();
        initApiService();

        // Check if category was passed from MainActivity
        Intent intent = getIntent();
        if (intent.hasExtra("categoryId") && intent.hasExtra("categoryName")) {
            int categoryId = intent.getIntExtra("categoryId", -1);
            String categoryName = intent.getStringExtra("categoryName");

            // Set toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(categoryName);
            }

            currentCategoryId = categoryId;
            resetAndLoadProducts();
        } else {
            loadCategories();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilCategory = findViewById(R.id.tilCategory);
        actvCategory = findViewById(R.id.actvCategory);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvSortInfo = findViewById(R.id.tvSortInfo);
        rvProducts = findViewById(R.id.rvProducts);
        llEmptyState = findViewById(R.id.llEmptyState);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Loading item should span both columns
                return adapter.getItemViewType(position) == 1 ? 2 : 1;
            }
        });

        rvProducts.setLayoutManager(layoutManager);
        adapter = new ProductGridAdapter(this);
        rvProducts.setAdapter(adapter);

        adapter.setOnProductClickListener(product -> {
            // TODO: Handle product click (navigate to product detail)
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        // Add scroll listener for lazy loading
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMorePages) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (visibleItemCount + pastVisiblesItems >= totalItemCount - 4) {
                        loadProducts(currentCategoryId, false);
                    }
                }
            }
        });
    }

    private void setupCategoryDropdown() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvCategory.setAdapter(categoryAdapter);

        actvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < categories.size()) {
                    Category selectedCategory = categories.get(position);
                    currentCategoryId = selectedCategory.getId();
                    resetAndLoadProducts();
                }
            }
        });
    }

    private void initApiService() {
        apiService = ApiClient.getApiService();
    }

    private void loadCategories() {
        showLoading(true);

        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());

                    List<String> categoryNames = new ArrayList<>();
                    for (Category category : categories) {
                        categoryNames.add(category.getName());
                    }

                    categoryAdapter.clear();
                    categoryAdapter.addAll(categoryNames);
                    categoryAdapter.notifyDataSetChanged();

                    // Auto-select first category if available
                    if (!categories.isEmpty()) {
                        actvCategory.setText(categories.get(0).getName(), false);
                        currentCategoryId = categories.get(0).getId();
                        resetAndLoadProducts();
                    }
                } else {
                    showError("Không thể tải danh sách danh mục");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void resetAndLoadProducts() {
        currentPage = 0;
        hasMorePages = true;
        adapter.setProducts(new ArrayList<>());
        loadProducts(currentCategoryId, true);
    }

    private void loadProducts(int categoryId, boolean isFirstLoad) {
        if (isLoading || categoryId == -1) return;

        isLoading = true;

        if (isFirstLoad) {
            showLoading(true);
        } else {
            adapter.showLoading();
        }

        apiService.getProductsByCategoryPaged(
                categoryId,
                currentPage,
                ITEMS_PER_PAGE,
                "price,asc"
        ).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                isLoading = false;

                if (isFirstLoad) {
                    showLoading(false);
                } else {
                    adapter.hideLoading();
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    if (isFirstLoad) {
                        adapter.setProducts(products);
                        totalProductCount = products.size();
                    } else {
                        adapter.addProducts(products);
                        totalProductCount += products.size();
                    }

                    updateProductCount();

                    // Check if there are more pages
                    hasMorePages = products.size() == ITEMS_PER_PAGE;
                    currentPage++;

                    // Show/hide empty state
                    if (adapter.getItemCount() == 0) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }

                } else {
                    if (isFirstLoad) {
                        showEmptyState(true);
                    }
                    showError("Không thể tải danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                isLoading = false;

                if (isFirstLoad) {
                    showLoading(false);
                    showEmptyState(true);
                } else {
                    adapter.hideLoading();
                }

                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void updateProductCount() {
        String countText = "Tìm thấy " + totalProductCount + " sản phẩm";
        if (!hasMorePages && currentPage > 1) {
            countText += " (Tất cả)";
        }
        tvProductCount.setText(countText);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(show ? View.GONE : View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        llEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}