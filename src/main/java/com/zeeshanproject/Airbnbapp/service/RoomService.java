package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long HotelId,RoomDto roomDto);

    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    RoomDto getRoomById(Long roomId);

    RoomDto updateRoom(Long hotelId, Long roomId, RoomDto roomDto);

    void deleteRoomById(Long roomId);
}
