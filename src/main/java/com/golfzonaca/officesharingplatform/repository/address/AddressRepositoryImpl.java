package com.golfzonaca.officesharingplatform.repository.address;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}
