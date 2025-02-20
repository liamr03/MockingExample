package com.example.payment;

/** Represents the response from a payment API */
public class PaymentApiResponse {
    private final boolean success;

    public PaymentApiResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
