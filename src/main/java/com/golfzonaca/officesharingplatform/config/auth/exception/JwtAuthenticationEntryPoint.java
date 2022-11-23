package com.golfzonaca.officesharingplatform.config.auth.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String path = request.getServletPath();
        // TODO: 1. isHeader
        if (request.getHeader("Authorization") != null) {
            Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));
            // id가 맞는지 확인 후 accessToken 이 만료됐다면 재발급 or error처리
            // TODO: 2. isToken
            if (token.isPresent()) {
                // TODO: 3. isAccessTokenOrRefreshToken
                String refreshPath = "/auth/refresh";
                if (path.equals(refreshPath)) {
                    String refreshToken = token.get();
                    if (JwtManager.validateRefreshJwt(refreshToken)) {
                        Long userId = JwtManager.getIdByToken(token.get());
                        String accessToken = createNewEncodedAccessToken(userId);
                        JsonObject jsonObject = createJsonObject(accessToken, refreshToken);
                        PrintWriter out = response.getWriter();
                        response.setStatus(HttpStatus.ACCEPTED.value());
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        out.print(jsonObject);
                        out.flush();
                        log.info("Create New AccessToken");
                    } else {
                        log.info("RefreshToken Expired");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Login Again");
                    }
                } else {
                    if (!JwtManager.validateJwt(token.get())) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "expiration token");
                    }
                }
            } else {
                log.warn("유효하지 않은 토큰 입니다.");
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            log.warn("헤더에 토큰 값이 없습니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private JsonObject createJsonObject(String accessToken, String refreshToken) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("accessToken", accessToken);
        jsonObject.addProperty("refreshToken", refreshToken);
        return jsonObject;
    }

    private String createNewEncodedAccessToken(Long userId) throws JsonProcessingException {
        Jwt newToken = JwtManager.createJwt(userId.toString(), "access");
        return newToken.getEncoded();
    }
}
