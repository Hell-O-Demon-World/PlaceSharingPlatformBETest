package com.golfzonaca.officesharingplatform.repository.room;

import com.golfzonaca.officesharingplatform.domain.Room;

import java.util.List;

public interface RoomRepository {

    List<Room> findRoomByPlaceIdAndRoomKindId(Long placeId, Long roomKindId);
    
    List<Long> findRoomIdByPlaceIdAndRoomTypeId(long placeId, long roomTypeId);

    int countRoomQuantityByPlaceId(long placeId, long roomTypeId);

    List<Integer> findRoomTypeByPlaceId(long placeId);
}
