package com.zeeshanproject.Airbnbapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationDto {

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

}
