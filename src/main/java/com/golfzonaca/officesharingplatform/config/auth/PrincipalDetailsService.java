package com.golfzonaca.officesharingplatform.config.auth;

import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        Set<GrantedAuthority> grantedAuthorityList = new HashSet<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        grantedAuthorityList.add(simpleGrantedAuthority);

        return new PrincipalDetails(user.getMail(), user.getPassword(), grantedAuthorityList);
    }
}
