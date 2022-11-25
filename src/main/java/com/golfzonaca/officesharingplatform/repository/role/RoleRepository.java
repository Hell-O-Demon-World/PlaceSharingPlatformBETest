package com.golfzonaca.officesharingplatform.repository.role;

import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByRole(RoleType role);

}
