package com.golfzonaca.officesharingplatform.repository.reservation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationRepositoryImpl {
    private final JPAQueryFactory queryFactory;
}
