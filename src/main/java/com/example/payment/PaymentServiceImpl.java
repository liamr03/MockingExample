package com.example.payment;

/** Mock implementation of PaymentService */
public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentApiResponse charge(String apiKey, double amount) {
        System.out.println("Charging payment: " + amount);
        return new PaymentApiResponse(true); // Simulating a successful charge
    }
}
