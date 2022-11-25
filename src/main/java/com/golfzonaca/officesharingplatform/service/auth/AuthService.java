package com.golfzonaca.officesharingplatform.service.auth;


import com.golfzonaca.officesharingplatform.domain.User;

public interface AuthService {

    boolean isAvailableEmail(String email);
    void join(User user);
    public boolean isAvailableTelNum(String phoneNumber);

}
