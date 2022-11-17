package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomKindRepository extends JpaRepository<RoomKind, Long>, RoomKindRepositoryCustom {
    Long findIdByRoomType(String roomType);

    RoomKind findById(long roomId);
}
