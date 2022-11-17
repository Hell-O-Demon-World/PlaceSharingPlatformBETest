package com.golfzonaca.officesharingplatform.repository.room;

import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.repository.mybatis.RoomMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MyBatisRoomRepository {
    private final RoomMapper roomMapper;

    public List<Room> findRoomByPlaceIdAndRoomKindId(Long placeId, Long roomKindId) {
        return roomMapper.findRoomByPlaceIdAndRoomKindId(placeId, roomKindId);
    }

    public List<Long> findRoomIdByPlaceIdAndRoomTypeId(long placeId, long roomTypeId) {
        return roomMapper.findRoomIdByPlaceIdAndRoomTypeId(placeId, roomTypeId);
    }

    public int countRoomQuantityByPlaceId(long placeId, long roomTypeId) {
        return roomMapper.countRoomQuantityByPlaceId(placeId, roomTypeId);
    }

    public List<Integer> findRoomTypeByPlaceId(long placeId) {
        return roomMapper.findRoomTypeByPlaceId(placeId);
    }
}
