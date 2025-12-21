package com.bank.recommendations;

import com.bank.customers.Customer;

public class InvestmentStrategy implements RecommendationStrategy {

    @Override
    public String generateRecommendation(Customer customer) {
        // مثال: إذا رصيد عالي، اقترح استثمار
        if (customer.getTotalBalance().getAmount().compareTo(new java.math.BigDecimal("5000.00")) > 0) {
            return "Recommendation: Consider investing in stocks for higher returns.";
        }
        return "Recommendation: Build more savings before investing.";
    }
}