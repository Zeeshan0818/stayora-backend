package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.Config.RazorpayConfig;
import com.zeeshanproject.Airbnbapp.Exception.ResourceNotFoundException;
import com.zeeshanproject.Airbnbapp.Exception.UnAutherisedException;
import com.zeeshanproject.Airbnbapp.Strategy.PricingService;
import com.zeeshanproject.Airbnbapp.dto.BookingDto;
import com.zeeshanproject.Airbnbapp.dto.BookingRequest;
import com.zeeshanproject.Airbnbapp.dto.GuestDto;
import com.zeeshanproject.Airbnbapp.dto.PaymentVerificationDto;
import com.zeeshanproject.Airbnbapp.entity.*;
import com.zeeshanproject.Airbnbapp.entity.enums.BookingStatus;
import com.zeeshanproject.Airbnbapp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;
    private final RazorpayConfig razorpayConfig;
    private final ModelMapper modelMapper;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initialisBooking(BookingRequest bookingRequest) {

        log.info(
                "Initialising booking for hotel : {}, room: {}, date {}-{}",
                bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate()
        );

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + bookingRequest.getHotelId()
                        )
                );

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with ID: " + bookingRequest.getRoomId()
                        )
                );

        List<Inventory> inventoryList =
                inventoryRepository.findAndLockAvailableInventory(
                        room.getId(),
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate(),
                        bookingRequest.getRoomCount()
                );

        long dayCount =
                ChronoUnit.DAYS.between(
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate()
                ) + 1;

        if (inventoryList.size() != dayCount) {
            throw new IllegalArgumentException("Room not available anymore");
        }

        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(
                    inventory.getReservedCount() + bookingRequest.getRoomCount()
            );
        }

        inventoryRepository.saveAll(inventoryList);

        BigDecimal priceForOneRoom =
                pricingService.calculateTotalPrice(inventoryList);

        BigDecimal totalPrice =
                priceForOneRoom.multiply(
                        BigDecimal.valueOf(bookingRequest.getRoomCount())
                );

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkinDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(
            Long bookingId,
            List<GuestDto> guestDtoList
    ) {

        log.info("Adding guest for booking Id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with Id: " + bookingId
                        )
                );

        User user = getCurrentUser();

        // Compare user IDs instead of User entity objects
        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAutherisedException(
                    "Booking does not belong to this user with id: "
                            + user.getId()
            );
        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException(
                    "Booking is not under RESERVED state, cannot add guest"
            );
        }

        for (GuestDto guestDto : guestDtoList) {

            Guest guest = modelMapper.map(guestDto, Guest.class);

            guest.setUser(user);

            guest = guestRepository.save(guest);

            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public String initiatePayments(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        User user = getCurrentUser();

        // Compare user IDs instead of User entity objects
        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAutherisedException(
                    "Booking does not belong to this user with id: "
                            + user.getId()
            );
        }

        if (booking.getBookingStatus() != BookingStatus.GUEST_ADDED) {
            throw new IllegalStateException(
                    "Guests must be added before initiating payment."
            );
        }

        String paymentResponse =
                checkoutService.getCheckoutSession(
                        booking,
                        frontendUrl + "/payments/success",
                        frontendUrl + "/payments/failure"
                );

        booking.setBookingStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        return paymentResponse;
    }

    @Override
    @Transactional
    public void verifyPayment(PaymentVerificationDto dto) {

        boolean verified = checkoutService.verifyPayment(dto);

        if (!verified) {
            throw new IllegalArgumentException(
                    "Payment verification failed"
            );
        }

        Booking booking = bookingRepository
                .findByPaymentSessionId(dto.getRazorpayOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found for order id: "
                                        + dto.getRazorpayOrderId()
                        )
                );

        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Booking has already been confirmed."
            );
        }

        booking.setPaymentId(dto.getRazorpayPaymentId());

        booking.setBookingStatus(BookingStatus.CONFIRMED);

        List<Inventory> inventoryList =
                inventoryRepository.findByRoomAndDateBetween(
                        booking.getRoom(),
                        booking.getCheckinDate(),
                        booking.getCheckOutDate()
                );

        for (Inventory inventory : inventoryList) {

            inventory.setReservedCount(
                    inventory.getReservedCount()
                            - booking.getRoomsCount()
            );

            inventory.setBookedCount(
                    inventory.getBookedCount()
                            + booking.getRoomsCount()
            );
        }

        inventoryRepository.saveAll(inventoryList);

        bookingRepository.save(booking);

        log.info(
                "Payment verified successfully for booking id: {}",
                booking.getId()
        );
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        User user = getCurrentUser();

        // Compare user IDs instead of User entity objects
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new UnAutherisedException(
                    "Booking does not belong to user with id: "
                            + user.getId()
            );
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be cancelled."
            );
        }

        if (booking.getCheckinDate().isBefore(
                LocalDate.now().plusDays(1)
        )) {
            throw new IllegalStateException(
                    "Booking cannot be cancelled within 24 hours of check-in."
            );
        }

        List<Inventory> inventoryList =
                inventoryRepository.findByRoomAndDateBetween(
                        booking.getRoom(),
                        booking.getCheckinDate(),
                        booking.getCheckOutDate()
                );

        for (Inventory inventory : inventoryList) {

            inventory.setBookedCount(
                    inventory.getBookedCount()
                            - booking.getRoomsCount()
            );
        }

        inventoryRepository.saveAll(inventoryList);

        checkoutService.refundPayment(
                booking.getPaymentId(),
                booking.getAmount()
        );

        booking.setBookingStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        log.info(
                "Booking cancelled successfully. Booking Id: {}",
                bookingId
        );
    }

    public Boolean hasBookingExpired(Booking booking) {

        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {

        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}