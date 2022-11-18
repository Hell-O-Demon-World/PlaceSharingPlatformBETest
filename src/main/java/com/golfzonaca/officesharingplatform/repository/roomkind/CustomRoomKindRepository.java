package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomRoomKindRepository implements RoomKindRepository {
    private final SpringDataJpaRoomKindRepository jpaRoomKindRepository;

    @Override
    public Long findIdByRoomType(String roomType) {
        return jpaRoomKindRepository.findIdByRoomTypeLike(roomType);
    }

    @Override
    public RoomKind findById(Long roomId) {
        return jpaRoomKindRepository.findById(roomId).get();
    }
}
