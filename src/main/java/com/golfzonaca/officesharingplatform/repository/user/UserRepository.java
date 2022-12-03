package com.golfzonaca.officesharingplatform.repository.user;


import com.golfzonaca.officesharingplatform.domain.User;

import java.util.List;


public interface UserRepository {
    User save(User user);

    User findById(Long id);

    User findByMailLike(String email);

    Boolean isUniqueTel(String tel);

    Boolean isUniqueEmail(String email);

    Integer countContainByEmail(String email);

    List<User> findAll();

    List<User> findAll(UserSearchCond cond);

    Boolean validateUserByUserId(Long userId);
}
