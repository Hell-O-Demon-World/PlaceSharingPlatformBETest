package com.golfzonaca.officesharingplatform.config.auth.handler;

import com.golfzonaca.officesharingplatform.config.auth.filter.servlet.JwtHttpServletProvider;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtHttpServletProvider jwtHttpServletProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User findUser = userRepository.findByMailLike(authentication.getPrincipal().toString());
        Jwt accessJwt = JwtManager.createAccessJwt(findUser.getId());
        Jwt refreshJwt = JwtManager.createRefreshJwt(findUser.getId());
        JsonObject json = new JsonObject();
        json.addProperty("accessToken", accessJwt.getEncoded());
        json.addProperty("refreshToken", refreshJwt.getEncoded());

        jwtHttpServletProvider.responseJsonObject(response, json);
    }
}
