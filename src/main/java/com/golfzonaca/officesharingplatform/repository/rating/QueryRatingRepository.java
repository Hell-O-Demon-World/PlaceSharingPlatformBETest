package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QRating.rating;

@Repository
@Transactional
public class QueryRatingRepository {
    private final JPAQueryFactory query;

    public QueryRatingRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<Rating> findAllByUser(User user, Integer page) {
        return query
                .selectFrom(rating)
                .innerJoin(rating.reservation.user)
                .where(rating.reservation.user.eq(user))
                .orderBy(rating.ratingTime.desc())
                .offset(8L * (page - 1))
                .limit(8)
                .fetch();
    }

    public List<Rating> findAllByPlace(Place place, long page) {
        return query
                .selectFrom(rating)
                .innerJoin(rating.reservation.room.place)
                .where(rating.reservation.room.place.eq(place))
                .orderBy(rating.ratingTime.desc())
                .offset(4L * (page - 1))
                .limit(4)
                .fetch();
    }
}
