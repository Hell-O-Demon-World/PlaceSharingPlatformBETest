package com.golfzonaca.officesharingplatform.repository.role;

import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaRoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleType(RoleType roleType);
}
