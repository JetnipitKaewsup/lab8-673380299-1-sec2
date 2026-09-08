package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.ProductDetail;

public interface ProductDetailRepository extends JpaRepository <ProductDetail, Long>{

    
}