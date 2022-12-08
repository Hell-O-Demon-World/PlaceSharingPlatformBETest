package com.golfzonaca.officesharingplatform.service.auth;


import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.web.auth.form.EmailForm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {

    boolean isAvailableEmail(String email);
    void join(User user);
    boolean isAvailableTelNum(String phoneNumber);
    void sendMail(EmailForm emailForm) throws NoSuchAlgorithmException, UnsupportedEncodingException;

}
