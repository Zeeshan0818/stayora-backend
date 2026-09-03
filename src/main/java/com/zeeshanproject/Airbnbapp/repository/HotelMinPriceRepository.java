package com.zeeshanproject.Airbnbapp.repository;

import com.zeeshanproject.Airbnbapp.dto.HotelPriceDto;
import com.zeeshanproject.Airbnbapp.entity.Hotel;
import com.zeeshanproject.Airbnbapp.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    @Query("""
            SELECT new com.zeeshanproject.Airbnbapp.dto.HotelPriceDto(i.hotel, AVG(i.price))
            FROM HotelMinPrice i
            WHERE LOWER(i.hotel.city) = LOWER(:city)
            AND i.date BETWEEN :startDate AND :endDate
            AND i.hotel.active = true
            GROUP BY i.hotel
           """)
    Page<HotelPriceDto> findHotelWithAvailableInventroy(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}