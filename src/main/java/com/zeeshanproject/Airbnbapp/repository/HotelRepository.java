package com.zeeshanproject.Airbnbapp.repository;

import com.zeeshanproject.Airbnbapp.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Page<Hotel> findByActiveTrue(Pageable pageable);

    Page<Hotel> findByActiveTrueAndCityIgnoreCase(
            String city,
            Pageable pageable
    );
}