package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.dto.HotelPriceDto;
import com.zeeshanproject.Airbnbapp.dto.HotelSearchRequest;
import com.zeeshanproject.Airbnbapp.entity.Inventory;
import com.zeeshanproject.Airbnbapp.entity.Room;
import com.zeeshanproject.Airbnbapp.repository.HotelMinPriceRepository;
import com.zeeshanproject.Airbnbapp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        // Create one inventory record for every day for one year
        for (; !today.isAfter(endDate); today = today.plusDays(1)) {

            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Room room) {

        log.info(
                "Deleting the inventories of rooms with ID: {}",
                room.getId()
        );

        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(
            HotelSearchRequest hotelSearchRequest
    ) {

        log.info(
                "Searching hotels for {} city, from {} to {}",
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate()
        );

        Pageable pageable = PageRequest.of(
                hotelSearchRequest.getPage(),
                hotelSearchRequest.getSize()
        );

        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate()
        ) + 1;

        Page<HotelPriceDto> hotelPage =
                hotelMinPriceRepository.findHotelWithAvailableInventroy(
                        hotelSearchRequest.getCity(),
                        hotelSearchRequest.getStartDate(),
                        hotelSearchRequest.getEndDate(),
                        hotelSearchRequest.getRoomCount(),
                        dateCount,
                        pageable
                );

        return hotelPage;
    }
}