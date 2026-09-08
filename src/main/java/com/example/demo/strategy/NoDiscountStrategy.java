package com.example.demo.strategy;



public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculate(double price){
        return price;
    }
}