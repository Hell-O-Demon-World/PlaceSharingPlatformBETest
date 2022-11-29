package com.golfzonaca.officesharingplatform.repository.role;

import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslQueryRepository implements RoleRepository {

    private final SpringJpaRoleRepository jpaRepository;
    private final QueryRoleRepository query;

    @Override
    public Optional<Role> findByRole(RoleType roleType) {
        return query.findByRole(roleType);
    }
}
