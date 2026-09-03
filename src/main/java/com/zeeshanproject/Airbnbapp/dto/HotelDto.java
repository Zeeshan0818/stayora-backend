package com.zeeshanproject.Airbnbapp.dto;

import com.zeeshanproject.Airbnbapp.entity.HotelContactinfo;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
public class HotelDto {

    private Long id;

    private String name;

    private String city;

    private String[] photos;

    private String[] amenities;

    private HotelContactinfo hotelContactinfo;

    private Boolean active;
}
