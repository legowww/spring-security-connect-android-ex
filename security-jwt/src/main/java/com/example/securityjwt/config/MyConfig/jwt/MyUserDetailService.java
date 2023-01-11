package com.example.securityjwt.config.MyConfig.jwt;

import com.example.securityjwt.model.User;
import com.example.securityjwt.model.entity.UserEntity;
import com.example.securityjwt.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailService implements UserDetailsService {
    private final UserEntityRepository userEntityRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("[위치]MyUserDetailService.loadUserByUsername");
        return userEntityRepository.findByUsername(username).map(User::fromEntity).orElseThrow(() ->
                new UsernameNotFoundException("unfounded username"));
    }
}
