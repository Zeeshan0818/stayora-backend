package com.zeeshanproject.Airbnbapp.repository;

import com.zeeshanproject.Airbnbapp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
}
