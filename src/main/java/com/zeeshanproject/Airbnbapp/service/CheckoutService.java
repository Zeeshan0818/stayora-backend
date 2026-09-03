package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.PaymentVerificationDto;
import com.zeeshanproject.Airbnbapp.entity.Booking;

import java.math.BigDecimal;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

    Boolean verifyPayment(PaymentVerificationDto dto);

    void refundPayment(String paymentId, BigDecimal amount);
}
