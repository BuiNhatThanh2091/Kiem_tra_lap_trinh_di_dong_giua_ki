package com.example.ktra_gki_ltdd.repository;

import com.example.ktra_gki_ltdd.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
