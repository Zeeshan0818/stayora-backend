package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.HotelDto;
import com.zeeshanproject.Airbnbapp.dto.HotelPriceDto;
import com.zeeshanproject.Airbnbapp.dto.HotelSearchRequest;
import com.zeeshanproject.Airbnbapp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
