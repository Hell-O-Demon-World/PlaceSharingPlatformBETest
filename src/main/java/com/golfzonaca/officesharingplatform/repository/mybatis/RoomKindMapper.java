package com.golfzonaca.officesharingplatform.repository.mybatis;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoomKindMapper {

    Long findIdByRoomType(String roomType);

    RoomKind findById(long roomKindId);
}
