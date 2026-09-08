package com.example.demo.strategy;


public class SeasonalSaleStrategy implements DiscountStrategy {
    @Override
    public double calculate(double price) {
        return price * 0.8;
    }
}