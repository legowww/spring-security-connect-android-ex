package com.example.securityjwt.config.MyConfig;


import com.example.securityjwt.config.MyConfig.jwt.JwtAuthenticationFilter;
import com.example.securityjwt.config.MyConfig.jwt.JwtAuthorizationFilter;
import com.example.securityjwt.config.MyConfig.jwt.MyUserDetailService;
import com.example.securityjwt.exception.CustomEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

// ==참조한 블로그==
// https://stir.tistory.com/275 - formLogin() 설명 읽어볼 것

// https://hungseong.tistory.com/67 - AuthenticationProvider 과 AuthenticationSuccessHandler 를 사용한 방식

// https://github.com/codingspecialist/Springboot-Security-JWT-Easy/tree/version2 - AuthenticationProvider 커스터 마이징 방법 기술

// https://wildeveloperetrain.tistory.com/56 - 예외 처리
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MySecurityConfig {
    private final MyUserDetailService myUserDetailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${jwt.secret-key}")
    private String key;
    @Value("${jwt.token-expired-time-ms}")
    private Long expiredTimeMs;


    /**
     * formLogin().disable():
     * 폼 로그인은 쉽게 말하자면 UserDetailsService 의 loadByUser() 만을 사용하고 나머지는
     * 스프링 시큐리티가 알아서 세션을 통한 로그인 작업을 수행하는 작업이다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers("/", "/login", "/join", "/formLogin").permitAll()
                        .anyRequest().authenticated()
                .and()
                    .apply(new MyCustomDsl())
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new CustomEntryPoint()) // 토큰없이 접속하면 엔트리 포인트, 참고로 @ControllerAdvice 는 필터와 인터셉터 예외를 처리 할 수 없다.
                .and()
                    .build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                .addFilter(new JwtAuthenticationFilter(authenticationManager, key, expiredTimeMs)) //인증(login)
                .addFilterBefore(new JwtAuthorizationFilter(key, myUserDetailService), BasicAuthenticationFilter.class); //인가(request)
        }
    }

    // DaoAuthenticationProvider 는 AuthenticationProvider 를 상속받았고 실제로 내가 사용하는 인증 프로바이더이다.
    // UsernameNotFoundException 이 아닌 BadCredentialsException 으로 생성되는 오류해결하기 위해 인증 프로바이더 커스텀
    // https://wildeveloperetrain.tistory.com/56
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        daoAuthenticationProvider.setUserDetailsService(myUserDetailService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }
}
