package com.golfzonaca.officesharingplatform.config.auth.filter;

import com.golfzonaca.officesharingplatform.config.auth.token.IdPwAuthenticationToken;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader("Authorization");

        if(jwt != null && JwtManager.validateJwt(jwt)){
            String id = JwtManager.getInfo(jwt, "id");
            Authentication authentication = getAuthentication(id);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);
    }

    private Authentication getAuthentication(String id) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(id);
        return new IdPwAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }
}
