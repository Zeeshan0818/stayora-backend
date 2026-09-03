package com.zeeshanproject.Airbnbapp.dto;

import com.zeeshanproject.Airbnbapp.entity.Hotel;
import com.zeeshanproject.Airbnbapp.entity.Room;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingRequest {

    private Long hotelId;
    private Long roomId;
    private String city;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomCount;

}
