package com.golfzonaca.officesharingplatform.config.auth.filter;

import com.golfzonaca.officesharingplatform.config.auth.PrincipalDetailsService;
import com.golfzonaca.officesharingplatform.config.auth.token.IdPwAuthenticationToken;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader("Authorization");
        if (jwt != null && JwtManager.isAccessToken(jwt) && JwtManager.validateJwt(jwt)) {
            System.out.println("ok");
            String id = JwtManager.getInfo(jwt, "id");
            Authentication authentication = getAuthentication(Long.valueOf(id));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);
    }

    private Authentication getAuthentication(Long id) {

        UserDetails userDetails = principalDetailsService.loadUserByUserId(id);
        return new IdPwAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }
}
