//<!--Dao Tuan Duy - 23162011-->
package com.example.kiem_tra_giua_ki_di_dong.model;

import java.util.List;


public class ProductResponse {
    private boolean success;
    private List<Product> data;
    private String message;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<Product> getData() { return data; }
    public void setData(List<Product> data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
