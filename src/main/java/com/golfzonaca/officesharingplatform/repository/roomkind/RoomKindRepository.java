package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;

import java.util.List;

public interface RoomKindRepository {
    Long findIdByRoomType(String roomType);

    RoomKind findById(Long roomId);

    RoomKind findByRoomType(String roomType);

    List<RoomKind> findAll();
}
