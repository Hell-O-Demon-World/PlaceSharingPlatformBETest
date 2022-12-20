package com.golfzonaca.officesharingplatform.auth.handler;

import com.golfzonaca.officesharingplatform.auth.filter.servlet.JwtHttpServletProvider;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Calendar;

@Component
@Transactional
@RequiredArgsConstructor
public class PasswordMismatchHandler {
    private final JwtHttpServletProvider jwtHttpServletProvider;

    public void onAuthenticationPasswordFailure(HttpServletResponse response, String pw1, String pw2) throws IOException {
        if (!matchesPw1Pw2(pw1, pw2)) {
            JsonObject json = new JsonObject();
            json.addProperty("timestamp", String.valueOf(Calendar.getInstance().getTime()));
            json.addProperty("status", HttpServletResponse.SC_UNAUTHORIZED);
            json.addProperty("message", "입력된 비밀번호가 일치하지 않습니다.");
            jwtHttpServletProvider.responseJsonObject(response, HttpStatus.UNAUTHORIZED, json);
        }
    }

    private boolean matchesPw1Pw2(String pw1, String pw2) {
        return pw1.equals(pw2);
    }
}
