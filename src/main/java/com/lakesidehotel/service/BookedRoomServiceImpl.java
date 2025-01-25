package com.lakesidehotel.service;

import com.lakesidehotel.exception.InvalidBookingRequestException;
import com.lakesidehotel.model.BookedRoom;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.repository.BookedRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookedRoomServiceImpl implements BookedRoomService{

    @Autowired
    private BookedRoomRepository bookedRoomRepository;

    @Autowired
    private RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(long roomId) {
        return bookedRoomRepository.findByRoomId(roomId);
    }

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookedRoomRepository.findAll();
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookedRoomRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("No booking found with booking code :" + confirmationCode));
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existedBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest,existedBookings);
        if (roomIsAvailable){
            room.addBooking(bookingRequest);
            bookedRoomRepository.save(bookingRequest);
        }else {
            throw new InvalidBookingRequestException("Sorry, This room is not available for the selected date");
        }
        return bookingRequest.getBookingConfirmationCode();
    }


    @Override
    public void cancalBooking(Long bookingId) {
        bookedRoomRepository.deleteById(bookingId);
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existedBookings) {
        return existedBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

}
