package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.Exception.ResourceNotFoundException;
import com.zeeshanproject.Airbnbapp.Exception.UnAutherisedException;
import com.zeeshanproject.Airbnbapp.dto.HotelDto;
import com.zeeshanproject.Airbnbapp.dto.HotelInfoDto;
import com.zeeshanproject.Airbnbapp.dto.RoomDto;
import com.zeeshanproject.Airbnbapp.entity.Hotel;
import com.zeeshanproject.Airbnbapp.entity.Room;
import com.zeeshanproject.Airbnbapp.entity.User;
import com.zeeshanproject.Airbnbapp.repository.HotelRepository;
import com.zeeshanproject.Airbnbapp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final PricingUpdateService pricingUpdateService;
    private final ModelMapper modelMapper;

    @Override
    public Page<HotelDto> browseHotels(String city, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Hotel> hotels;

        if (city == null || city.isBlank()) {
            hotels = hotelRepository.findByActiveTrue(pageable);
        } else {
            hotels = hotelRepository.findByActiveTrueAndCityIgnoreCase(
                    city.trim(),
                    pageable
            );
        }

        return hotels.map(
                hotel -> modelMapper.map(hotel, HotelDto.class)
        );
    }

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {

        log.info(
                "Creating a new hotel with name: {}",
                hotelDto.getName()
        );

        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);

        // New hotels are inactive by default
        hotel.setActive(false);

        // Get currently logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Set logged-in user as owner
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);

        log.info(
                "Created hotel with ID: {}",
                hotel.getId()
        );

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelbyId(Long id) {

        log.info(
                "Getting the hotel with ID: {}",
                id
        );

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + id
                        )
                );

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info("CURRENT USER ID: {}", user.getId());
        log.info("CURRENT USER EMAIL: {}", user.getEmail());
        log.info("HOTEL OWNER ID: {}", hotel.getOwner().getId());
        log.info("HOTEL OWNER EMAIL: {}", hotel.getOwner().getEmail());

        // Check ownership
        if (!user.getId().equals(hotel.getOwner().getId())) {

            throw new UnAutherisedException(
                    "This user does not own this hotel with id: " + id
            );
        }

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {

        log.info(
                "Updating the hotel with ID: {}",
                id
        );

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + id
                        )
                );

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info("CURRENT USER ID: {}", user.getId());
        log.info("HOTEL OWNER ID: {}", hotel.getOwner().getId());

        // Check ownership using user IDs
        if (!user.getId().equals(hotel.getOwner().getId())) {

            throw new UnAutherisedException(
                    "This user does not own this hotel with id: " + id
            );
        }

        modelMapper.map(hotelDto, hotel);

        hotel.setId(id);

        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + hotelId
                        )
                );

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map(element ->
                        modelMapper.map(element, RoomDto.class)
                )
                .toList();

        return new HotelInfoDto(
                modelMapper.map(hotel, HotelDto.class),
                rooms
        );
    }

    @Override
    @Transactional
    public void activeHotel(Long hotelId) {

        log.info(
                "Activating hotel with ID: {}",
                hotelId
        );

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + hotelId
                        )
                );

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info(
                "Logged-in user ID: {}",
                user.getId()
        );

        log.info(
                "Hotel owner ID: {}",
                hotel.getOwner().getId()
        );

        log.info(
                "Hotel ID: {}",
                hotelId
        );

        // Check hotel ownership
        if (!user.getId().equals(hotel.getOwner().getId())) {

            throw new UnAutherisedException(
                    "This user does not own this hotel with id: " + hotelId
            );
        }

        // Activate hotel
        hotel.setActive(true);

        // Create inventory for every room
        for (Room room : hotel.getRooms()) {

            log.info(
                    "Creating inventory for room ID: {}",
                    room.getId()
            );

            inventoryService.initializeRoomForAYear(room);
        }

        // Create/update HotelMinPrice records
        pricingUpdateService.updateHotelPrice(hotel);

        log.info(
                "Hotel {} activated successfully",
                hotelId
        );
    }

    @Override
    @Transactional
    public void DeleteHotelById(Long id) {

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + id
                        )
                );

        // Delete inventories and rooms
        for (Room room : hotel.getRooms()) {

            inventoryService.deleteAllInventories(room);

            roomRepository.deleteById(room.getId());
        }

        // Delete hotel
        hotelRepository.deleteById(id);

        log.info(
                "Hotel {} deleted successfully",
                id
        );
    }
}