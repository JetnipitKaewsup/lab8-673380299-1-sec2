package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductDetailRepository;
import com.example.demo.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    
    // Constructor Injection
    public ProductService(ProductRepository productRepository, ProductDetailRepository productDetailRepositroy) {
        this.productRepository = productRepository;
        this.productDetailRepository = productDetailRepositroy;
    }

    // Read All
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Read One
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Add
    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null");
        }

        ProductDetail detail = product.getDetail();

        if (detail != null) {
            detail.setProduct(product);
        }

        if (product.getReviews() != null) {
            for (Review review : product.getReviews()) {
                review.setProduct(product);
            }
        }
        return productRepository.save(product);
    }

    // Update
    public Product updateProduct(Long id, Product product) {
        // Find by id
        Product oldProduct = productRepository.findById(id).orElse(null);

        // Update product fields

        oldProduct.setName(product.getName());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setBrand(product.getBrand());
        oldProduct.setStock(product.getStock());
        oldProduct.setDiscountType(product.getDiscountType());
        oldProduct.setPrice(product.getPrice());

        // Update product detail
        updateProductDetail(oldProduct, product.getDetail());

        return productRepository.save(oldProduct);

    }

    // Update product detail
    private void updateProductDetail(
            Product oldProduct,
            ProductDetail newDetail) {

        if (newDetail == null) {
            return;
        }

        ProductDetail oldDetail = oldProduct.getDetail();

        // กรณี Product เดิมยังไม่มี Detail
        if (oldDetail == null) {

            ProductDetail detail = new ProductDetail();

            copyDetailData(newDetail, detail);

            detail.setProduct(oldProduct);
            oldProduct.setDetail(detail);

            productDetailRepository.save(detail);

            return;
        }

        // กรณีมี Detail เดิมอยู่แล้ว
        copyDetailData(newDetail, oldDetail);

        productDetailRepository.save(oldDetail);
    }

    // Copy product detail

    private void copyDetailData(
            ProductDetail source,
            ProductDetail target) {

        target.setDescription(
                source.getDescription());

        target.setWarranty(
                source.getWarranty());

        target.setWeight(
                source.getWeight());

        target.setDimensions(
                source.getDimensions());

        target.setManufacturedCountry(
                source.getManufacturedCountry());
    }

    // Delete
    public boolean deleteProduct(Long id) {

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }

        return false;
    }
}