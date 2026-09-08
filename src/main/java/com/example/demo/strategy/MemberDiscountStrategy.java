package com.example.demo.strategy;



public class MemberDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculate(double price) {
        return price * 0.9;
    }
}