package com.bank.recommendations;

import com.bank.customers.Customer;
import java.math.BigDecimal;

public class SpendingStrategy implements RecommendationStrategy {
    @Override
    public String generateRecommendation(Customer customer) {
        if (customer.getTotalBalance().getAmount().compareTo(new BigDecimal("1000.00")) < 0) {
            return "Recommendation: Reduce spending and save more!";
        }
        return "Recommendation: Your spending is balanced.";
    }
}