package com.golfzonaca.officesharingplatform.service.auth;


import com.golfzonaca.officesharingplatform.service.auth.dto.SignUpInfoDto;
import com.golfzonaca.officesharingplatform.web.auth.form.EmailForm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {

    String findUserEmail(String name, String tel);

    void join(SignUpInfoDto signUpInfoDto);

    boolean isAvailableTelNum(String phoneNumber);

    void sendMail(EmailForm emailForm) throws NoSuchAlgorithmException, UnsupportedEncodingException;

    void createNewPassword(String email, String newPassword);
}
