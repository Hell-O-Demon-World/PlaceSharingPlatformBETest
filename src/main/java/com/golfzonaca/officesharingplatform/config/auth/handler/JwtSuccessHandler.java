package com.golfzonaca.officesharingplatform.config.auth.handler;

import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try (PrintWriter writer = response.getWriter()) {
            Jwt accessJwt = JwtManager.createJwt(userRepository.findByMailLike(authentication.getPrincipal().toString()).getId().toString(), "access");
            Jwt refreshJwt = JwtManager.createJwt(userRepository.findByMailLike(authentication.getPrincipal().toString()).getId().toString(), "refresh");

            JsonObject json = new JsonObject();
            json.addProperty("accessToken", accessJwt.getEncoded());
            json.addProperty("refreshToken", refreshJwt.getEncoded());
            response.setStatus(HttpStatus.ACCEPTED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

            writer.write(json.toString());

        } catch (IOException e) {
            log.error("fail to process file={}", e);
        }
    }
}
