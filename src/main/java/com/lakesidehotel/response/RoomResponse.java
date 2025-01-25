package com.lakesidehotel.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
@Data
@NoArgsConstructor
public class RoomResponse {
    private long id;
    private String roomType;
    private BigDecimal Price;
    private boolean isBooked;
    private String photo;
    private List<BookingResponse> bookings;

    public RoomResponse(long id, String roomType, BigDecimal price) {
        this.id = id;
        this.roomType = roomType;
        Price = price;
    }

    public RoomResponse(long id, String roomType, BigDecimal price,
                        boolean isBooked, byte[] photoBytes, List<BookingResponse> bookings) {
        this.id = id;
        this.roomType = roomType;
        Price = price;
        this.isBooked = isBooked;
        this.photo = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;
        this.bookings = bookings;
    }
}
