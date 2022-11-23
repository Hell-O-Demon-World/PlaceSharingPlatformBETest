package com.golfzonaca.officesharingplatform.config.auth.filter.servlet;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtHttpServletProvider implements TokenHttpServletProvider {
    @Override
    public void responseJsonObject(HttpServletResponse response, JsonObject jsonObject) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(HttpStatus.ACCEPTED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(jsonObject);
            out.flush();
        } catch (IOException e) {
            log.error("fail to process file={}", e);
        }
    }
}
