package com.example.payment;

 /** Interface för Database */
    public interface DatabaseService {
        void savePayment(double amount, String status);
    }
