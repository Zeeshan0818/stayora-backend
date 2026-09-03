package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.HotelDto;
import com.zeeshanproject.Airbnbapp.dto.HotelInfoDto;
import com.zeeshanproject.Airbnbapp.entity.Hotel;
import org.jspecify.annotations.Nullable;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelbyId(Long id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    void DeleteHotelById(Long id);

    void activeHotel(Long HotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
