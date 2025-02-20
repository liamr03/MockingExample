package com.example.payment;

    /** Interface för Email */
    public interface EmailService {
        void sendPaymentConfirmation(String email, double amount);
    }
