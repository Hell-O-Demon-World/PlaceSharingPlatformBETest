package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class UserTest {
    @Autowired
    EntityManager em;

    @Autowired
    MileageService mileageService;

    @Test
    public void joinUser() throws Exception {
        // given
        String username = "memberA";
        String email = "abc@test.com";
        String password = "password";
        String phoneNumber = "01012341234";
        String job = "학생";
        String userPlace = "";
        Mileage mileage = mileageService.join();

        Role userRole = Role.userRole();
        em.persist(userRole);
        // when
        User user = User.joinUser(username, email, password, phoneNumber, job, userPlace, mileage);
        em.persist(user);
        em.flush();
        em.clear();
        // then
        User findUser = em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", "abc@test.com")
                .getSingleResult();

        assertThat(findUser.getUsername()).isEqualTo("memberA");
        assertThat(findUser.getEmail()).isEqualTo("abc@test.com");
        assertThat(findUser.getPassword()).isEqualTo("password");
        assertThat(findUser.getPhoneNumber()).isEqualTo("01012341234");
        assertThat(findUser.getJob()).isEqualTo("학생");
        assertThat(findUser.getUserPlace()).isBlank();
    }

}