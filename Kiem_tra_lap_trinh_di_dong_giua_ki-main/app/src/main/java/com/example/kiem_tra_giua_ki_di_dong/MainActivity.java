package com.example.kiem_tra_giua_ki_di_dong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.kiem_tra_giua_ki_di_dong.model.Category;
import com.example.kiem_tra_giua_ki_di_dong.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_categories);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, 
                                                            LinearLayoutManager.HORIZONTAL, 
                                                            false);
        recyclerView.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(categoryAdapter);

        fetchCategoriesFromApi();
    }

    private void fetchCategoriesFromApi() {
        List<Category> fetchedList = new ArrayList<>();
        
        fetchedList.add(new Category(1, "Beef", "https://img.example.com/beef.jpg"));
        fetchedList.add(new Category(2, "Chicken", "https://img.example.com/chicken.jpg"));
        fetchedList.add(new Category(3, "Dessert", "https://img.example.com/dessert.jpg"));
        fetchedList.add(new Category(4, "Seafood", "https://img.example.com/seafood.jpg"));
        fetchedList.add(new Category(5, "Vegetables", "https://img.example.com/vegetables.jpg"));
        fetchedList.add(new Category(6, "Noodles", "https://img.example.com/noodles.jpg"));
        fetchedList.add(new Category(7, "Soup", "https://img.example.com/soup.jpg"));
        fetchedList.add(new Category(8, "Pizza", "https://img.example.com/pizza.jpg"));
        fetchedList.add(new Category(9, "Drinks", "https://img.example.com/drinks.jpg"));
        fetchedList.add(new Category(10, "Breakfast", "https://img.example.com/breakfast.jpg"));
        
        categoryAdapter.setCategoryList(fetchedList);
        Log.d("Categories", "Đã tải thành công " + fetchedList.size() + " danh mục.");
    }
}
