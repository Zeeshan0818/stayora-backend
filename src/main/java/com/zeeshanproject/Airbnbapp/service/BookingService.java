package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.BookingDto;
import com.zeeshanproject.Airbnbapp.dto.BookingRequest;
import com.zeeshanproject.Airbnbapp.dto.GuestDto;
import com.zeeshanproject.Airbnbapp.dto.PaymentVerificationDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface BookingService {
     BookingDto initialisBooking(BookingRequest bookingRequest);

    @Nullable BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void verifyPayment(PaymentVerificationDto dto);

    void cancelBooking(Long bookingId);
}
