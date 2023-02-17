package com.golfzonaca.officesharingplatform.service.auth;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class JpaAuthServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MileageService mileageService;

    @Test
    public void 회원가입() throws Exception {
        // given
        Mileage mileage = mileageService.join();
        User user = User.joinUser("memberA", "memberA@test.com", "00000000", "01000000000", "학생", "", mileage);
        // when
        User savedUser = userRepository.save(user);
        // then
        assertThat(savedUser).isEqualTo(user);
    }

}