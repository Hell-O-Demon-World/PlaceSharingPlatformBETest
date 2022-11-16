package com.golfzonaca.officesharingplatform.repository.mybatis;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    void save(User user);

    User findById(long id);

    User findByEmail(String email);

    int countContainByEmail(String email);

    User update(@Param("id") long id, @Param("updateParam") UserInfoDto updateParam);

    List<User> findAll();

    int validateUserByUserId(long userId);
}
