package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.repository.mybatis.RoomKindMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisRoomKindRepository implements RoomKindRepository {
    private final RoomKindMapper roomKindMapper;

    @Override
    public Long findIdByRoomType(String roomType) {
        Long typeId = roomKindMapper.findIdByRoomType(roomType);
        if (typeId == null) {
            return -1L;
        }
        return typeId;
    }

    @Override
    public RoomKind findById(long roomKindId) {
        return roomKindMapper.findById(roomKindId);
    }
}
