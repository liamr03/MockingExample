package com.example.payment;

public class PaymentProcessor {
    private final PaymentService paymentService;
    private final DatabaseService databaseService;
    private final EmailService emailService;
    private final String apiKey;

    public PaymentProcessor(PaymentService paymentService, DatabaseService databaseService, EmailService emailService, String apiKey) {
        this.paymentService = paymentService;
        this.databaseService = databaseService;
        this.emailService = emailService;
        this.apiKey = apiKey;
    }

    public boolean processPayment(double amount) {
        PaymentApiResponse response = paymentService.charge(apiKey, amount);

        if (response.isSuccess()) {
            databaseService.savePayment(amount, "SUCCESS");
            emailService.sendPaymentConfirmation("user@example.com", amount);
        }

        return response.isSuccess();
    }
}
