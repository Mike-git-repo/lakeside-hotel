package com.lakesidehotel.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponse {
    private long id;


    private LocalDate checkInDate;


    private LocalDate checkOutDate;


    private String guestFullName;


    private String guestEmail;


    private int NumOfAdults;


    private int NumOfChildren;


    private int totalNumOfGuest;


    private String bookingConfirmationCode;

    private RoomResponse room;

    public BookingResponse(long id, LocalDate checkInDate, LocalDate checkOutDate,
                           String bookingConfirmationCode) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}
