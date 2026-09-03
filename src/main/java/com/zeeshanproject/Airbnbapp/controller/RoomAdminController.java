package com.zeeshanproject.Airbnbapp.controller;

import com.zeeshanproject.Airbnbapp.dto.RoomDto;
import com.zeeshanproject.Airbnbapp.service.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomServiceImpl roomService;

    // Create a new room
    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId,@RequestBody RoomDto roomDto) {
        RoomDto room = roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    // Get all rooms in a hotel
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId) {
        List<RoomDto> rooms = roomService.getAllRoomsInHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    // Get a specific room
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomsById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        RoomDto roomDto = roomService.getRoomById(roomId);
        return ResponseEntity.ok(roomDto);
    }

    // Update a room
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long hotelId, @PathVariable Long roomId, @RequestBody RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoom(hotelId, roomId, roomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    // Delete a room
    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDto> deleteRoomsById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }
}