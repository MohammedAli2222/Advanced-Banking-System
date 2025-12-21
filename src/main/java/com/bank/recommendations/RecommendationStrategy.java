package com.bank.recommendations;

import com.bank.customers.Customer;

public interface RecommendationStrategy {
    String generateRecommendation(Customer customer);
}