package com.golfzonaca.officesharingplatform.config.auth.filter;

import com.golfzonaca.officesharingplatform.config.auth.token.IdPwAuthenticationToken;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonIdPwAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    public JsonIdPwAuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        Map<String, Object> parseJsonMap = parseJsonMap(request);

        String id = (String) parseJsonMap.get("id");
        String pw = (String) parseJsonMap.get("pw");

        IdPwAuthenticationToken idPwAuthenticationToken = new IdPwAuthenticationToken(id, pw);
        idPwAuthenticationToken.setDetails(super.authenticationDetailsSource.buildDetails(request));

        return super.getAuthenticationManager().authenticate(idPwAuthenticationToken);
    }

    private Map<String, Object> parseJsonMap(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        GsonJsonParser gsonJsonParser = new GsonJsonParser();
        return gsonJsonParser.parseMap(body);
    }
}
