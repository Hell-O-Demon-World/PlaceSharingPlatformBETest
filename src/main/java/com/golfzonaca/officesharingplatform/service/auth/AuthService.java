package com.golfzonaca.officesharingplatform.service.auth;


import com.golfzonaca.officesharingplatform.domain.User;

public interface AuthService {

    boolean emailCheck(String email);
    Boolean join(User user);

}
