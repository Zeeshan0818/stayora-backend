package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.HotelDto;
import com.zeeshanproject.Airbnbapp.dto.HotelInfoDto;
import org.springframework.data.domain.Page;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelbyId(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void DeleteHotelById(Long id);

    void activeHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);

    Page<HotelDto> browseHotels(String city, int page, int size);
}