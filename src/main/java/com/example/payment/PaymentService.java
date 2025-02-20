package com.example.payment;

/** Interface for payment processing */
public interface PaymentService {
    PaymentApiResponse charge(String apiKey, double amount);
}
