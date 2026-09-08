package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.strategy.DiscountContext;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String category;
    private String brand;
    private int stock;
    private Double price;
    private String discountType;

    // ── 1:1 กับ ProductDetail ──
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "detail_id", referencedColumnName = "id")
    private ProductDetail detail;

    // ── 1:N กับ Review ──
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // Constructor
    public Product() {

    }

    public Product(long id, String name, String category, String brand,
            int stock, Double price, String discountType,
            ProductDetail detail, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.stock = stock;
        this.price = price;
        this.discountType = discountType;
        this.detail = detail;
        this.reviews = reviews;
    }

    public Product(String name, String category, String brand,
            int stock, Double price, String discountType) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.stock = stock;
        this.price = price;
        this.discountType = discountType;
        this.reviews = new ArrayList<>();
    }

    // Strategy Pattern
     public double getDiscountedPrice() {

        if (price == null) {
            return 0.0;
        }

        return DiscountContext
                .getStrategy(discountType)
                .calculate(price);
    }

    // Getter / Setter

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;  
    }

    public ProductDetail getDetail() {
        return detail;
    }

    public void setDetail(ProductDetail detail) {
        this.detail = detail;
        if (detail != null) {
            detail.setProduct(this);
        }
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        if (reviews != null) {
        for (Review review : reviews) {
            review.setProduct(this);
        }
    }
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setProduct(this);
    }

   
}