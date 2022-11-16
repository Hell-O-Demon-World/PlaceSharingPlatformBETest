package com.golfzonaca.officesharingplatform.repository.user;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.mybatis.UserMapper;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        userMapper.save(user);
        return user;
    }

    @Override
    public User findById(long id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByEmail(String email) {
        if (userMapper.findByEmail(email) == null) {
            throw new AuthenticationCredentialsNotFoundException(email);
        }
        return userMapper.findByEmail(email);
    }

    @Override
    public int countContainByEmail(String email) {
        return userMapper.countContainByEmail(email);
    }

    @Override
    public User update(long id, UserInfoDto updateParam) {
        return userMapper.update(id, updateParam);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public Boolean validateUserByUserId(long userId) {
        if (userMapper.validateUserByUserId(userId) == 1) {
            return true;
        } else {
            return false;
        }
    }
}
