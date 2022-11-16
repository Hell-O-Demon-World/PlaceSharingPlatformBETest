package com.golfzonaca.officesharingplatform.repository.user;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.UserInfoDto;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User findById(long id);

    User findByEmail(String email);

    int countContainByEmail(String email);

    User update(long id, UserInfoDto updateParam);

    List<User> findAll();

    Boolean validateUserByUserId(long userId);
}
