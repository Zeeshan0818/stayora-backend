package com.zeeshanproject.Airbnbapp.controller;

import com.zeeshanproject.Airbnbapp.dto.HotelDto;
import com.zeeshanproject.Airbnbapp.dto.HotelInfoDto;
import com.zeeshanproject.Airbnbapp.dto.HotelPriceDto;
import com.zeeshanproject.Airbnbapp.dto.HotelSearchRequest;
import com.zeeshanproject.Airbnbapp.service.HotelService;
import com.zeeshanproject.Airbnbapp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelDto>> browseHotels(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "9") Integer size
    ) {
        return ResponseEntity.ok(
                hotelService.browseHotels(city, page, size)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(
            @RequestParam String city,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer roomCount,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        HotelSearchRequest request = new HotelSearchRequest();

        request.setCity(city);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setRoomCount(roomCount);
        request.setPage(page);
        request.setSize(size);

        return ResponseEntity.ok(
                inventoryService.searchHotels(request)
        );
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }

}
