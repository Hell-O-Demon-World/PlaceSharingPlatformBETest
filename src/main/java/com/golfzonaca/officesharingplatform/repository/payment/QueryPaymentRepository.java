package com.golfzonaca.officesharingplatform.repository.payment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@Transactional
public class QueryPaymentRepository {

    private final JPAQueryFactory query;

    public QueryPaymentRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }
    
}