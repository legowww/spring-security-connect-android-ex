package com.example.securityjwt.service;


import com.example.securityjwt.model.User;
import com.example.securityjwt.model.entity.UserEntity;
import com.example.securityjwt.repository.UserEntityRepository;
import com.example.securityjwt.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-expired-time-ms}")
    private Long expiredTimeMs;


    @Transactional
    public User join(String username, String password) {
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(username, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    public User loadUserByUsername(String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).get();
        return User.fromEntity(userEntity);
    }

    public String login(String username, String password) {
        User user = loadUserByUsername(username);
        if (!encoder.matches(password, user.getPassword())) {
            return "password error";
        }
        String token = JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);
        return token;
    }
}