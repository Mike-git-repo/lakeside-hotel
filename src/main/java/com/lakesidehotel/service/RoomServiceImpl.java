package com.lakesidehotel.service;

import com.lakesidehotel.exception.InternalServerException;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService{

    @Autowired
    private RoomRepository roomrepository;
    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal price) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setPrice(price);
        if(!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }

        return roomrepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {

        return roomrepository.finDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomrepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(long roomId) throws Exception {
        Optional<Room> theRoom = roomrepository.findById(roomId);
        if(theRoom.isEmpty()){
            throw new Exception("Sorry, Room not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1,(int) photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom = roomrepository.findById(roomId);
        if(theRoom.isPresent()){
            roomrepository.deleteById(roomId);
        }

    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal price, byte[] photoBytes) throws Exception {
        Room room = roomrepository.findById(roomId).orElseThrow(() -> new Exception("Room not found"));
        if(roomType != null) room.setRoomType(roomType);
        if (price != null) room.setPrice(price);
        if(photoBytes != null && photoBytes.length > 0){
            try {
                room.setPhoto(new SerialBlob(photoBytes));
            }catch (SQLException e){
                throw new InternalServerException("Error updating room");
            }
        }

        return roomrepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomrepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomrepository.findAvailableRoomsByDatesAndType(checkInDate,checkOutDate,roomType);
    }
}
