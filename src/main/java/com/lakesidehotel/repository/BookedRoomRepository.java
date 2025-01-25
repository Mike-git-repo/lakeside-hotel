package com.lakesidehotel.repository;

import com.lakesidehotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookedRoomRepository extends JpaRepository<BookedRoom,Long> {


    List<BookedRoom> findByRoomId(long roomId);

    Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);
}
