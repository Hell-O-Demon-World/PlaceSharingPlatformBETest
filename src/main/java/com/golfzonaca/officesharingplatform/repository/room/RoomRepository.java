package com.golfzonaca.officesharingplatform.repository.room;


import com.golfzonaca.officesharingplatform.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {

    List<Room> findRoomByPlaceIdAndRoomKindId(Long placeId, Long roomKindId);

    List<Long> findRoomIdByPlaceIdAndRoomTypeId(Long placeId, Long roomKindId);

    int countRoomQuantityByPlaceId(Long placeId, Long roomKindId);

    List<Integer> findRoomTypeByPlaceId(Long placeId);

    List<Room> findAll(RoomSearchCond cond);
}
