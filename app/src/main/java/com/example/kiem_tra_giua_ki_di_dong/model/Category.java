package com.example.kiem_tra_giua_ki_di_dong.model;

public class Category {
    private int id;
    private String createdAt;
    private String description;
    private String name;

    public Category(int id, String createdAt, String description, String name) {
        this.id = id;
        this.createdAt = createdAt;
        this.description = description;
        this.name = name;
    }

    public Category(String createdAt, String description, String name) {
        this.createdAt = createdAt;
        this.description = description;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
