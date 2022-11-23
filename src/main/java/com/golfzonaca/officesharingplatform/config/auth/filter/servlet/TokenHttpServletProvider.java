package com.golfzonaca.officesharingplatform.config.auth.filter.servlet;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenHttpServletProvider {
    void responseJsonObject(HttpServletResponse response, JsonObject jsonObject) throws IOException;

}
