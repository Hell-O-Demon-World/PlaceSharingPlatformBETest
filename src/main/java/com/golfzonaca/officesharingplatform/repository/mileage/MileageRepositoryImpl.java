package com.golfzonaca.officesharingplatform.repository.mileage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MileageRepositoryImpl {
    private final JPAQueryFactory queryFactory;
}
