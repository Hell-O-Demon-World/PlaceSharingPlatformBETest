package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QPayment.payment;

@Repository
@Transactional
public class QueryPaymentRepository {

    private final JPAQueryFactory query;

    public QueryPaymentRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<Payment> getCancelInfo(long reservationId) {
        return query
                .selectFrom(payment)
                .where(payment.reservation.id.eq(reservationId))
                .fetch();
    }
}