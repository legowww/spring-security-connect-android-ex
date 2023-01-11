package com.example.securityjwt.config.apiConfig;


import com.example.securityjwt.config.apiConfig.filter.JwtTokenFilter;
import com.example.securityjwt.exception.CustomEntryPoint;
import com.example.securityjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.secret-key}")
    private String key;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .formLogin().disable() //JWT
                .httpBasic().disable() //JWT
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //JWT
                .and()
                .authorizeRequests()
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .mvcMatchers("/", "/join", "/login").permitAll()
                    .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomEntryPoint()) //만료된 토큰 전송 시
                .and()
                .build();
    }
}
