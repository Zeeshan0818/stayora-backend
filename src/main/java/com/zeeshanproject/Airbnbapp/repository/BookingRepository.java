package com.zeeshanproject.Airbnbapp.repository;

import com.zeeshanproject.Airbnbapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    Optional<Booking> findByPaymentSessionId(String paymentSessionId);
}
