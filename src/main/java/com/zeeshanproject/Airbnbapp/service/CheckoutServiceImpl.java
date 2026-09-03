package com.zeeshanproject.Airbnbapp.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.zeeshanproject.Airbnbapp.dto.PaymentVerificationDto;
import com.zeeshanproject.Airbnbapp.entity.Booking;
import com.zeeshanproject.Airbnbapp.entity.User;
import com.zeeshanproject.Airbnbapp.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService{

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BookingRepository bookingRepository;
    private final RazorpayClient razorpayClient;

    @Override
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl) {

        log.info("Creating Razorpay Order for booking with ID: {}", booking.getId());

        try {

            JSONObject options = new JSONObject();

            options.put("amount",
                    booking.getAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .longValue());

            options.put("currency", "INR");

            options.put("receipt", booking.getId().toString());

            options.put("notes", new JSONObject()
                    .put("hotel", booking.getHotel().getName())
                    .put("room", booking.getRoom().getType().toString()));

            Order order = razorpayClient.orders.create(options);

            booking.setPaymentSessionId(order.get("id"));
            bookingRepository.save(booking);

            log.info("Razorpay Order Created successfully for booking with ID: {}",
                    booking.getId());

            return order.toString();

        } catch (RazorpayException e) {
            throw new RuntimeException("Unable to create Razorpay Order", e);
        }
    }

    @Override
    public Boolean verifyPayment(PaymentVerificationDto dto) {
        try {

            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id", dto.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", dto.getRazorpayPaymentId());
            attributes.put("razorpay_signature", dto.getRazorpaySignature());

            return Utils.verifyPaymentSignature(attributes, keySecret);

        } catch (RazorpayException e) {
            log.error("Payment verification failed", e);
            return false;
        }
    }

    @Override
    public void refundPayment(String paymentId, BigDecimal amount) {
        try {

            JSONObject refundRequest = new JSONObject();

            refundRequest.put("amount",
                    amount.multiply(BigDecimal.valueOf(100)).longValue());

            refundRequest.put("speed", "normal");

            razorpayClient.payments
                    .refund(paymentId, refundRequest);

            log.info("Refund initiated successfully for paymentId: {}", paymentId);

        } catch (RazorpayException e) {
            throw new RuntimeException(
                    "Unable to initiate refund for paymentId: " + paymentId, e);
        }
    }
}
