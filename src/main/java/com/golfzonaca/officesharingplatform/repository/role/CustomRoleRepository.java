package com.golfzonaca.officesharingplatform.repository.role;

import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomRoleRepository implements RoleRepository {
    private final SpringDataJpaRoleRepository jpaRoleRepository;

    @Override
    public Role findByRoleType(RoleType roleType) {
        return jpaRoleRepository.findByRoleType(roleType);
    }
}
