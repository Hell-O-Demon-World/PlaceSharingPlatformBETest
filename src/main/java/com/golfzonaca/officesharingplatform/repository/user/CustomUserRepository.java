package com.golfzonaca.officesharingplatform.repository.user;

import com.golfzonaca.officesharingplatform.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id);
    }

    @Override
    public User findByMailLike(String email) {
        return jpaUserRepository.findByEmailLike(email);
    }

    @Override
    public Boolean isUniqueTel(String tel) {
        return !queryUserRepository.findByTelLike(tel).isPresent();
    }

    @Override
    public Boolean isContainByEmail(String email) {
        return queryUserRepository.isContainByEmail(email).isEmpty();
    }

    @Override
    public Integer countContainByEmail(String email) {
        return jpaUserRepository.countContainByMail(email);
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
    public Boolean validateUserByUserId(Long userId) {
        return queryUserRepository.isContainById(userId).isEmpty();
    }
}
