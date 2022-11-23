package com.golfzonaca.officesharingplatform.config.auth.filter.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.golfzonaca.officesharingplatform.config.auth.filter.servlet.JwtHttpServletProvider;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JwtHttpServletProvider jwtHttpServletProvider;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String path = request.getServletPath();
        // TODO: 1. isHeader
        if (request.getHeader("Authorization") != null) {
            Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));
            // TODO: 2. isToken
            if (token.isPresent()) {
                // TODO: 3. isAccessTokenOrRefreshToken
                String refreshPath = "/auth/refresh";
                if (path.equals(refreshPath)) {
                    String refreshToken = token.get();
                    if (JwtManager.validateRefreshJwt(refreshToken)) {
                        Jwt newAccessToken = getNewAccessToken(refreshToken);
                        JsonObject jsonObject = encodedTokenToJson(newAccessToken.getEncoded(), refreshToken);
                        log.info("JWTExpiredException::: Create New AccessToken");
                        jwtHttpServletProvider.responseJsonObject(response, jsonObject);
                    } else {
                        log.info("JWTExpiredException::: RefreshToken Expired");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Login Again");
                    }
                } else {
                    if (!JwtManager.validateJwt(token.get())) {
                        log.warn("JWTException::: AccessToken Expired");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "expiration token");
                    } else {
                        log.warn("JWTException::: Validate AccessToken = {}", false);
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "expiration token");
                    }
                }
            } else {
                log.warn("InvalidTokenException::: Token is Null");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            log.warn("HTTPHeaderException::: No Authorization Parameter in HttpHeader");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private Jwt getNewAccessToken(String refreshToken) throws JsonProcessingException {
        Long userId = JwtManager.getIdByToken(refreshToken);
        return JwtManager.createAccessJwt(userId);
    }

    private JsonObject encodedTokenToJson(String accessToken, String refreshToken) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("accessToken", accessToken);
        jsonObject.addProperty("refreshToken", refreshToken);
        return jsonObject;
    }
}
