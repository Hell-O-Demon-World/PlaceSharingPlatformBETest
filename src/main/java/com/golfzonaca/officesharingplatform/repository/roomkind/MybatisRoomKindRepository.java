package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.repository.mybatis.RoomKindMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MybatisRoomKindRepository {
    private final RoomKindMapper roomKindMapper;

    public Long findIdByRoomType(String roomType) {
        Long typeId = roomKindMapper.findIdByRoomType(roomType);
        if (typeId == null) {
            return -1L;
        }
        return typeId;
    }

    public RoomKind findById(long roomKindId) {
        return roomKindMapper.findById(roomKindId);
    }
}
