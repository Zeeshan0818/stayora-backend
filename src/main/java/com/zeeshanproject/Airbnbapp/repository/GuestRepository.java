package com.zeeshanproject.Airbnbapp.repository;

import com.zeeshanproject.Airbnbapp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest,Long> {
}
