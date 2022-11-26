package com.golfzonaca.officesharingplatform.repository.user;


import com.golfzonaca.officesharingplatform.domain.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);

    User findByMailLike(String email);

    Boolean isUniqueTel(String tel);

    Boolean isContainByEmail(String email);

    Integer countContainByEmail(String email);

    List<User> findAll();

    List<User> findAll(UserSearchCond cond);

    Boolean validateUserByUserId(Long userId);
}
