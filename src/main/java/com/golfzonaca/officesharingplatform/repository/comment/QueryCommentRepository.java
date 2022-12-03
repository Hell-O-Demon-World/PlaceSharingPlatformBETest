package com.golfzonaca.officesharingplatform.repository.comment;

import com.golfzonaca.officesharingplatform.domain.Comment;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QComment.comment;

@Repository
@Transactional
public class QueryCommentRepository {
    private final JPAQueryFactory query;

    public QueryCommentRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<Comment> findAllByPlace(Place place) {

        return query
                .selectFrom(comment)
                .where(likePlace(place))
                .fetch();
    }

    private BooleanExpression likePlace(Place place) {
        if (place != null) {
            return comment.rating.reservation.room.place.id.eq(place.getId());
        }
        return null;
    }
}