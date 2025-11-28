package com.example.ktra_gki_ltdd.repository;

import com.example.ktra_gki_ltdd.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Lấy danh sách sản phẩm theo id category
    List<Product> findByCategoryId(Long categoryId);
}
