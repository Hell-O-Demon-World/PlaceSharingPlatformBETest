package com.golfzonaca.officesharingplatform.repository.role;

import com.golfzonaca.officesharingplatform.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringJpaRoleRepository extends JpaRepository<Role, Long> {
}
