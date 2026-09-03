package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.Exception.ResourceNotFoundException;
import com.zeeshanproject.Airbnbapp.Exception.UnAutherisedException;
import com.zeeshanproject.Airbnbapp.dto.RoomDto;
import com.zeeshanproject.Airbnbapp.entity.Hotel;
import com.zeeshanproject.Airbnbapp.entity.Room;
import com.zeeshanproject.Airbnbapp.entity.User;
import com.zeeshanproject.Airbnbapp.repository.HotelRepository;
import com.zeeshanproject.Airbnbapp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long HotelId,RoomDto roomDto) {
        log.info("Creating a new Room in hotel with Id: {}",HotelId);
        Hotel hotel = hotelRepository
                .findById(HotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID: "+HotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAutherisedException(
                    "This user does not own this hotel with id: " + HotelId
            );
        }
        Room room = modelMapper.map(roomDto,Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if (hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with Id: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAutherisedException(
                    "This user does not own this hotel with id: " + hotelId
            );
        }

        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting all rooms in hotel with Id: {}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID: "+roomId));
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public RoomDto updateRoom(Long hotelId, Long roomId, RoomDto roomDto) {

        log.info("Updating room with Id: {} in hotel with Id: {}", roomId, hotelId);

        // Find the hotel
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + hotelId
                        ));

        // Get the currently logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Check if the user owns the hotel
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAutherisedException(
                    "This user does not own this hotel with id: " + hotelId
            );
        }

        // Find the room
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with ID: " + roomId
                        ));

        // Make sure the room belongs to the selected hotel
        if (!room.getHotel().getId().equals(hotelId)) {
            throw new UnAutherisedException(
                    "Room does not belong to this hotel"
            );
        }

        // Update room fields
        room.setType(roomDto.getType());
        room.setBasePrice(roomDto.getBasePrice());
        room.setPhotos(roomDto.getPhotos());
        room.setTotalCount(roomDto.getTotalCount());
        room.setCapacity(roomDto.getCapacity());
        room.setAmenities(roomDto.getAmenities());

        // Save the updated room
        Room updatedRoom = roomRepository.save(room);

        log.info("Room with Id: {} updated successfully", roomId);

        // Convert entity to DTO and return
        return modelMapper.map(updatedRoom, RoomDto.class);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting rooms in hotel with Id: {}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID: "+roomId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new UnAutherisedException(
                    "This user does not own this room with id: " + roomId
            );
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

}
