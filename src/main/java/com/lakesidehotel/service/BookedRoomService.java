package com.lakesidehotel.service;

import com.lakesidehotel.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookedRoomService {
    List<BookedRoom> getAllBookingsByRoomId(long roomId);

    List<BookedRoom> getAllBookings();

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancalBooking(Long bookingId);
}
