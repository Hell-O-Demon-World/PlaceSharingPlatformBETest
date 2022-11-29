package com.golfzonaca.officesharingplatform.repository.role;

import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.golfzonaca.officesharingplatform.domain.QRole.role;

@Repository
@Transactional
public class QueryRoleRepository {
    private final JPAQueryFactory query;

    public QueryRoleRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Optional<Role> findByRole(RoleType roleType) {
        return Optional.ofNullable(query
                .selectFrom(role)
                .where(likeRoleType(roleType))
                .fetchOne());
    }

    private BooleanExpression likeRoleType(RoleType roleType) {
        if (roleType != null) {
            return role.roleType.eq(roleType);
        }
        return null;
    }
}