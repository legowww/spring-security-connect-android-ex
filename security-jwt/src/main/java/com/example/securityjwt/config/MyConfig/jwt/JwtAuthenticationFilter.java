package com.example.securityjwt.config.MyConfig.jwt;

import com.example.securityjwt.controller.response.Response;
import com.example.securityjwt.model.User;
import com.example.securityjwt.util.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Spring Security 에는 UsernamePasswordAuthenticationFilter 필터가 존재한다.
// /login 요청해서 username, password 전송하면 (post)
// attemptAuthentication() 메서드 실행
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final String key;
    private final Long expiredTimeMs;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[위치]JwtAuthenticationFilter.attemptAuthentication");

        try {
            //Form ->
//            String username = request.getParameter("username");
//            String password = request.getParameter("password");

            // JSON → Java Object
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            log.info("user={}", user.toString());

            // ==AuthenticationManager and AuthenticationProvider ==
            // authenticate() 함수가 호출 되면 AuthenticationProvider 가 UserDetailsService 의
            // loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
            // UserDetails 를 리턴받아서 토큰의 두번째 파라메터(credential)과
            // UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
            // Authentication 객체를 만들어서 필터체인으로 리턴해준다.
            // 완성된 Authentication 객체는 권한이 부여되어 getAuthorities() 가능하다.

            // UsernameNotFoundException 를 사용하기 위해 AuthenticationProvider 를 커스텀하였다.
            // Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
            // Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.info("loadUserByUsername() 성공하여 authentication 성공적으로 생성");

            // authentication 를 리턴하면 스프링 시큐리티 세션에 authentication 을 저장하게 된다.
            return authentication;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // AuthenticationSuccessHandler 대체
    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다.
    // 여기서 JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("[위치]JwtAuthenticationFilter.successfulAuthentication");
        User user = (User) authResult.getPrincipal();
        log.info("user={}, 권한이 부여됐다.={}", user, user.getAuthorities());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String token = JwtTokenUtils.generateToken(user.getUsername(), key, expiredTimeMs);
        response.setHeader("Authorization", "Bearer " + token);

        //Java Object → JSON
        new ObjectMapper().writeValue(response.getWriter(), Response.success(token));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("[위치]JwtAuthenticationFilter.unsuccessfulAuthentication");
        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        if (failed instanceof UsernameNotFoundException) {
            log.info("UsernameNotFoundException");
            new ObjectMapper().writeValue(response.getWriter(), Response.error("UsernameNotFoundException: unregistered username"));
        }
        else if (failed instanceof BadCredentialsException) {
            log.info("BadCredentialsException");
            new ObjectMapper().writeValue(response.getWriter(), Response.error("BadCredentialsException: wrong username or password"));
        }
        else {
            new ObjectMapper().writeValue(response.getWriter(), Response.error("Unidentified Error"));
        }
    }
}
