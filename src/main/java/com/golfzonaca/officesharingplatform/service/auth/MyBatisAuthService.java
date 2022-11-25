package com.golfzonaca.officesharingplatform.service.auth;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;
import com.golfzonaca.officesharingplatform.repository.role.RoleRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MyBatisAuthService implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MileageService mileageService;
    private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean emailCheck(String email) {
        return userRepository.countContainByEmail(email) == 0;
    }

    @Override
    public Boolean join(User user) {
        boolean isJoin = true;

        if (!emailCheck(user.getEmail())) {
            return false;
        }

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Mileage mileage = mileageService.join();


        user.updatePassword(encPassword);
        user.updateMileage(mileage);
        Role role = roleRepository.findByRole(RoleType.ROLE_USER).orElseThrow(() -> new NoSuchElementException("권한을 부여할 수 없습니다."));
        user.setRole(role);

        userRepository.save(user);

        return isJoin;
    }

}
