package com.bank.recommendations;

import com.bank.customers.Customer;

public class RecommendationEngine {
    private RecommendationStrategy strategy;

    public RecommendationEngine(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public String generate(Customer customer) {
        return strategy.generateRecommendation(customer);
    }

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }
}