package com.example;

import com.example.payment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentProcessorTest {
    private PaymentService paymentService;
    private DatabaseService databaseService;
    private EmailService emailService;
    private PaymentProcessor paymentProcessor;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        databaseService = mock(DatabaseService.class);
        emailService = mock(EmailService.class);
        paymentProcessor = new PaymentProcessor(paymentService, databaseService, emailService, "test_api_key");
    }

    @Test
    void testSuccessfulPayment() {
        // Simulera lyckad betalning
        when(paymentService.charge(anyString(), anyDouble())).thenReturn(new PaymentApiResponse(true));

        boolean result = paymentProcessor.processPayment(100.0);

        assertTrue(result);
        verify(databaseService).savePayment(100.0, "SUCCESS");
        verify(emailService).sendPaymentConfirmation("user@example.com", 100.0);
    }

    @Test
    void testFailedPayment() {
        // Simulera misslyckad betalning
        when(paymentService.charge(anyString(), anyDouble())).thenReturn(new PaymentApiResponse(false));

        boolean result = paymentProcessor.processPayment(100.0);

        assertFalse(result);
        verify(databaseService, never()).savePayment(anyDouble(), anyString());
        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());
    }

    @Test
    void testIsSuccessTrue() {
        PaymentApiResponse response = new PaymentApiResponse(true);
        assertTrue(response.isSuccess(), "Response should be successful when true is passed");
    }

    @Test
    void testIsSuccessFalse() {
        PaymentApiResponse response = new PaymentApiResponse(false);
        assertFalse(response.isSuccess(), "Response should not be successful when false is passed");
    }

    @Test
    void testChargeReturnsSuccessfulResponse() {
        PaymentService paymentService = new PaymentServiceImpl();
        PaymentApiResponse response = paymentService.charge("dummy_api_key", 100.0);

        // Assert that the response is not null and indicates success
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "The mock implementation should return a successful response");
    }
}
