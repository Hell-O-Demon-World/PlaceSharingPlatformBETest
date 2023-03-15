package com.golfzonaca.officesharingplatform.service.auth;

import com.golfzonaca.officesharingplatform.controller.auth.form.EmailForm;
import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.role.RoleRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.auth.dto.SignUpInfoDto;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JpaAuthService implements AuthService {
    private final UserRepository userRepository;
    private final MileageService mileageService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Override
    public String findUserEmail(String name, String tel) {

        return userRepository.findByNameAndTelLike(name, tel).getEmail();
    }

    @Transactional
    @Override
    public void join(SignUpInfoDto signUpInfoDto) {

        String encPassword = bCryptPasswordEncoder.encode(signUpInfoDto.getPassword());
        Mileage mileage = mileageService.join();

        User user = User.joinUser(signUpInfoDto.getName(), signUpInfoDto.getEmail(), encPassword, signUpInfoDto.getPhoneNumber(), signUpInfoDto.getJob(), signUpInfoDto.getUserPreferType(), mileage);

        userRepository.save(user);
    }

    @Override
    public boolean isAvailableTelNum(String phoneNumber) {
        return userRepository.isUniqueTel(phoneNumber);
    }

    @Override
    public void sendMail(EmailForm emailForm) {
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailForm.getAddress());
        simpleMailMessage.setSubject(emailForm.getTitle());
        simpleMailMessage.setText(emailForm.getMessage());
        javaMailSender.send(simpleMailMessage);
    }

    @Transactional
    @Override
    public void createNewPassword(String email, String newPassword) {
        User findUser = userRepository.findByMailLike(email);
        findUser.changePassword(bCryptPasswordEncoder.encode(newPassword));
    }
}
