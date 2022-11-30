package com.golfzonaca.officesharingplatform.repository.room;


import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Room;

import java.util.List;

public interface RoomRepository {

    List<Room> findRoomByPlaceIdAndRoomKindId(Long placeId, Long roomKindId);

    List<Long> findRoomIdByPlaceIdAndRoomTypeId(Long placeId, Long roomKindId);

    int countRoomQuantityByPlaceId(Long placeId, Long roomKindId);

    List<Integer> findRoomTypeByPlaceId(Long placeId);

    List<Room> findAll(RoomSearchCond cond);

    List<Room> findRoomByPlaceAndRoomKind(Place place, String selectedType);

    List<Room> findRoomByPlaceIdAndRoomType(Long placeId, String selectedType);

    Room findById(Long resultRoomId);
}
