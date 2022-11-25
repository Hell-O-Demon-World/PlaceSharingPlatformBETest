package com.golfzonaca.officesharingplatform.service.auth;

import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.RefreshToken;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import com.golfzonaca.officesharingplatform.service.refreshtoken.CustomRefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomAuthService implements AuthService {
    private final UserRepository userRepository;
    private final MileageService mileageService;
    private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean isAvailableEmail(String email) {
        return userRepository.countContainByEmail(email) == 0;
    }

    @Override
    public void join(User user) {

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Mileage mileage = mileageService.join();
        user.setMileage(mileage);
        user.setPassword(encPassword);

        userRepository.save(user);
    }

    @Override
    public boolean isAvailableTelNum(String phoneNumber) {
        return userRepository.isUniqueTel(phoneNumber);
    }
}
