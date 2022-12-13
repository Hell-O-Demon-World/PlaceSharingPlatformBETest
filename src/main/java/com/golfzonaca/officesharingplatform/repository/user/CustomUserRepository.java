package com.golfzonaca.officesharingplatform.repository.user;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.exception.NonExistedUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomUserRepository implements UserRepository {
    private final SpringDataJpaUserRepository jpaUserRepository;
    private final QueryUserRepository queryUserRepository;

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return jpaUserRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("UserNotFoundException::: 존재하지 않는 회원입니다."));
    }

    @Override
    public User findByMailLike(String email) {
        return jpaUserRepository.findByEmailLike(email).orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException::: 존재하지 않는 메일입니다."));
    }

    @Override
    public Boolean isUniqueTel(String tel) {
        return queryUserRepository.findByTelLike(tel).isEmpty();
    }

    @Override
    public Boolean isUniqueEmail(String email) {
        return queryUserRepository.isContainByEmail(email).isEmpty();
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public List<User> findAll(UserSearchCond cond) {
        return queryUserRepository.findAll(cond);
    }

    @Override
    public void delete(Long userId) {
        jpaUserRepository.deleteById(userId);
    }
}
