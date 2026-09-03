package com.zeeshanproject.Airbnbapp.controller;

import com.zeeshanproject.Airbnbapp.dto.HotelDto;
import com.zeeshanproject.Airbnbapp.repository.HotelRepository;
import com.zeeshanproject.Airbnbapp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelRepository hotelRepository;

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a new Hotel with name: "+hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotelDto = hotelService.getHotelbyId(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId
            ,@RequestBody HotelDto hotelDto){
        HotelDto hotel = hotelService.updateHotelById(hotelId,hotelDto);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> DeleteHotelById(@PathVariable Long hotelId){
        hotelRepository.deleteById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping({"/{hotelId}/activate"})
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId){
        hotelService.activeHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
