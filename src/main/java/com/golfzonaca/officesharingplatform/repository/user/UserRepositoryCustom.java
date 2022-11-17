package com.golfzonaca.officesharingplatform.repository.user;

import com.golfzonaca.officesharingplatform.domain.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findByUsername(String name);
}
