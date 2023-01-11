package com.example.securityjwt.config.MyConfig.jwt;

import com.example.securityjwt.model.User;
import com.example.securityjwt.service.UserService;
import com.example.securityjwt.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// BasicAuthenticationFilter 대신 OncePerRequestFilter 를 사용하여 구현했음

// 시큐리티가 필터를 가지고 있는데 필터중에 BasicAuthenticationFilter 라는 것이 있음.
// 이 필터는 UsernamePasswordAuthenticationFilter 다음 순서에 존재함
// 인가 절차가 필요한 request 라면 permitAll() 과 상관 없이 이 필터는 항상 타게 된다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final String key;
    private final MyUserDetailService myUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[위치]JwtAuthorizationFilter.doFilterInternal");

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("jwtHeader={}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. header is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = header.split(" ")[1].trim();

            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("key is Expired");
                filterChain.doFilter(request, response);
                return;
            }

            String username = JwtTokenUtils.getUsername(token, key);
            log.info("userName={}", username);

            User user = (User) myUserDetailService.loadUserByUsername(username);
            // Authentication 객체를 강제로 만들고 그걸 SecurityContextHolder 에 저장
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
