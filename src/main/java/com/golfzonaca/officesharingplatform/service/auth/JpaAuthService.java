package com.golfzonaca.officesharingplatform.service.auth;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.MileageUpdate;
import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.MileageStatusType;
import com.golfzonaca.officesharingplatform.domain.type.RoleType;
import com.golfzonaca.officesharingplatform.repository.role.RoleRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import com.golfzonaca.officesharingplatform.web.auth.form.EmailForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JpaAuthService implements AuthService {
    private final UserRepository userRepository;
    private final MileageService mileageService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean isAvailableEmail(String email) {
        return userRepository.isUniqueEmail(email);
    }

    @Override
    public void join(User user) {

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Mileage mileage = mileageService.join();
        Role role = roleRepository.findByRoleType(RoleType.ROLE_USER);
        LocalDateTime joinDate = LocalDateTime.now();
        user.updatePassword(encPassword);
        user.updateMileage(mileage);
        user.updateRole(role);
        user.updateDate(joinDate);

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
}
