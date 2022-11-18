package com.golfzonaca.officesharingplatform.repository.user;

import com.golfzonaca.officesharingplatform.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.golfzonaca.officesharingplatform.domain.QUser.user;


@Repository
@Transactional
public class QueryUserRepository {
    private final JPAQueryFactory query;

    public QueryUserRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Optional<User> isContainByEmail(String email) {
        return Optional.ofNullable(query
                .select(user)
                .from(user)
                .where(user.email.like(email))
                .fetchFirst());
    }

    public Optional<User> isContainById(Long userId) {
        return Optional.ofNullable(query
                .select(user)
                .from(user)
                .where(user.id.eq(userId))
                .fetchFirst());
    }

    public List<User> findAll(UserSearchCond cond) {
        String name = cond.getName();
        String mail = cond.getMail();
        String job = cond.getJob();
        String phoneNumber = cond.getPhoneNumber();
        String userPlace = cond.getUserPlace();

        return query
                .select(user)
                .from(user)
                .where(likeName(name), likeMail(mail), likeJob(job), likePhoneNumber(phoneNumber), likeUserPlace(userPlace))
                .fetch();
    }

    private BooleanExpression likeName(String name) {
        if (StringUtils.hasText(name)) {
            return user.username.like("%" + name + "%");
        }
        return null;
    }

    private BooleanExpression likeMail(String mail) {
        if (StringUtils.hasText(mail)) {
            return user.email.like("%" + mail + "%");
        }
        return null;
    }

    private BooleanExpression likeJob(String job) {
        if (StringUtils.hasText(job)) {
            return user.job.like("%" + job + "%");
        }
        return null;
    }

    private BooleanExpression likePhoneNumber(String phoneNumber) {
        if (StringUtils.hasText(phoneNumber)) {
            return user.phoneNumber.like("%" + phoneNumber + "%");
        }
        return null;
    }

    private BooleanExpression likeUserPlace(String userPlace) {
        if (StringUtils.hasText(userPlace)) {
            return user.userPlace.like("%" + userPlace + "%");
        }
        return null;
    }
}
