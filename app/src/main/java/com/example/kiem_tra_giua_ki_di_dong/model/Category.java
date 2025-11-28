package com.example.kiem_tra_giua_ki_di_dong.model;

import com.example.kiem_tra_giua_ki_di_dong.R;

public class Category {
    private int id;
    private String name;
    private String description;
    private String createdAt;

    public Category() {}

    public Category(int id, String name, String description, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // METHOD MỚI: Trả về resource ID của hình ảnh từ drawable
    public int getImageResource() {
        switch (id) {
            case 1: // Cơm phần
                return R.drawable.ic_category_rice;
            case 2: // Phở / Bún / Mì
                return R.drawable.ic_category_noodles;
            case 3: // Lẩu
                return R.drawable.ic_category_hotpot;
            case 4: // Món chiên / nướng
                return R.drawable.ic_category_grilled;
            case 5: // Đồ uống
                return R.drawable.ic_category_drinks;
            case 6: // Tráng miệng
                return R.drawable.ic_category_dessert;
            case 7: // Ăn vặt văn phòng
                return R.drawable.ic_category_snacks;
            case 8: // Cơm healthy
                return R.drawable.ic_category_healthy;
            case 9: // Món chay
                return R.drawable.ic_category_vegetarian;
            case 10: // Combo gia đình
                return R.drawable.ic_category_family;
            default:
                return R.drawable.ic_category_rice;
        }
    }
}