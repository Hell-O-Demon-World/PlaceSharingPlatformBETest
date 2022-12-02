package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.exception.NonExistedRoomKindException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomRoomKindRepository implements RoomKindRepository {
    private final SpringDataJpaRoomKindRepository jpaRoomKindRepository;

    private final QueryRoomKindRepository queryRoomKindRepository;

    @Override
    public Long findIdByRoomType(String roomType) {
        return jpaRoomKindRepository.findIdByRoomTypeLike(roomType);
    }

    @Override
    public RoomKind findById(Long roomId) {
        return jpaRoomKindRepository.findById(roomId).get();
    }

    @Override
    public RoomKind findByRoomType(String roomType) {
        return queryRoomKindRepository.findByRoomType(roomType).orElseThrow(NonExistedRoomKindException::new);
    }

    @Override
    public List<RoomKind> findAll() {
        return jpaRoomKindRepository.findAll();
    }

}
