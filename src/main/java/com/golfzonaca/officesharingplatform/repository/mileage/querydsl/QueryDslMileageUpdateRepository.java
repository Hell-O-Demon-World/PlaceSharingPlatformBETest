package com.golfzonaca.officesharingplatform.repository.mileage.querydsl;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.MileageTransactionUsage;
import com.golfzonaca.officesharingplatform.domain.MileageUpdate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QMileageUpdate.mileageUpdate;

@Repository
public class QueryDslMileageUpdateRepository {
    private final JPAQueryFactory query;

    public QueryDslMileageUpdateRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<MileageUpdate> findMileageUpdateAllLikeUserAndExpireDate(Mileage mileage, LocalDateTime currentLocalDateTime) {
        return query.selectFrom(mileageUpdate)
                .where(mileageUpdate.mileage.eq(mileage), mileageUpdate.expireDate.after(currentLocalDateTime))
                .fetch();
    }
}
