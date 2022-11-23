package com.golfzonaca.officesharingplatform.config;

import com.golfzonaca.officesharingplatform.config.auth.PrincipalDetailsService;
import com.golfzonaca.officesharingplatform.config.auth.exception.JwtAuthenticationEntryPoint;
import com.golfzonaca.officesharingplatform.config.auth.filter.JsonIdPwAuthenticationProcessingFilter;
import com.golfzonaca.officesharingplatform.config.auth.filter.JwtAuthenticationFilter;
import com.golfzonaca.officesharingplatform.config.auth.handler.JwtSuccessHandler;
import com.golfzonaca.officesharingplatform.config.auth.provider.IdPwAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
@RequiredArgsConstructor
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final PrincipalDetailsService principalDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtSuccessHandler jwtSuccessHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private static final RequestMatcher LOGIN_REQUEST_MATCHER = new AntPathRequestMatcher("/auth/signin", "POST");
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JsonIdPwAuthenticationProcessingFilter jsonIdPwAuthenticationProcessingFilter() throws Exception {
        JsonIdPwAuthenticationProcessingFilter jsonAuthenticationFilter = new JsonIdPwAuthenticationProcessingFilter(LOGIN_REQUEST_MATCHER);
        jsonAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(jwtSuccessHandler);
        return jsonAuthenticationFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
       return new IdPwAuthenticationProvider(principalDetailsService,
                passwordEncoder(),
                new SimpleAuthorityMapper());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/mypage", "/auth/refresh").hasRole("USER");
        http.addFilterAt(jsonIdPwAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                .addFilterBefore(jwtAuthenticationFilter, JsonIdPwAuthenticationProcessingFilter.class);
        http.userDetailsService(principalDetailsService);

        return http.build();
    }

}
