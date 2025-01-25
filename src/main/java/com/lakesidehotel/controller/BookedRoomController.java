package com.lakesidehotel.controller;

import com.lakesidehotel.exception.InvalidBookingRequestException;
import com.lakesidehotel.model.BookedRoom;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.response.BookingResponse;
import com.lakesidehotel.response.RoomResponse;
import com.lakesidehotel.service.BookedRoomService;
import com.lakesidehotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookedRoomController {

    @Autowired
    private BookedRoomService bookedRoomService;

    @Autowired
    private RoomService roomService;


    @GetMapping("/all-bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookedRoom> bookings = bookedRoomService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoom booking : bookings){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);

    }



    @GetMapping("/confirmation/{confirmationcode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookedRoom booking = bookedRoomService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest){
       try{
           String confirmationCode = bookedRoomService.saveBooking(roomId,bookingRequest);
           return ResponseEntity.ok("Room booked successfully! Your booking confirmation code is: " + confirmationCode);
       }catch (InvalidBookingRequestException e){
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancalBooking(@PathVariable Long bookingId){
        bookedRoomService.cancalBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(theRoom.getId(), theRoom.getRoomType(), theRoom.getPrice());

        return new BookingResponse(booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),booking.getGuestEmail(), booking.getNumOfAdults(),
                booking.getNumOfChildren(),booking.getTotalNumOfGuest(), booking.getBookingConfirmationCode(), room);
    }


}
