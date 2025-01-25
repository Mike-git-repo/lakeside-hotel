package com.lakesidehotel.service;

import com.lakesidehotel.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface RoomService {
    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal price) throws SQLException, IOException;

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(long roomId) throws Exception;

    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId, String roomType, BigDecimal price, byte[] photoBytes) throws Exception;

    Optional<Room> getRoomById(Long roomId);

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
