package com.golfzonaca.officesharingplatform.service.user;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        return findUser.orElse(null);
    }
}
