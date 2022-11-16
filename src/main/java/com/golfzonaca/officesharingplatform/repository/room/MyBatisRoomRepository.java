package com.golfzonaca.officesharingplatform.repository.room;

import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.repository.mybatis.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisRoomRepository implements RoomRepository {
    private final RoomMapper roomMapper;

    @Override
    public List<Room> findRoomByPlaceIdAndRoomKindId(Long placeId, Long roomKindId) {
        return roomMapper.findRoomByPlaceIdAndRoomKindId(placeId, roomKindId);
    }

    @Override
    public List<Long> findRoomIdByPlaceIdAndRoomTypeId(long placeId, long roomTypeId) {
        return roomMapper.findRoomIdByPlaceIdAndRoomTypeId(placeId, roomTypeId);
    }

    @Override
    public int countRoomQuantityByPlaceId(long placeId, long roomTypeId) {
        return roomMapper.countRoomQuantityByPlaceId(placeId, roomTypeId);
    }

    @Override
    public List<Integer> findRoomTypeByPlaceId(long placeId) {
        return roomMapper.findRoomTypeByPlaceId(placeId);
    }
}
