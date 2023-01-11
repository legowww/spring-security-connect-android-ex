package com.example.securityjwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
// https://github.com/jwtk/jjwt#jws-key-hmacsha
public class JwtTokenUtils {

    public static String getUsername(String token, String key) {
        return extractClaims(token, key).get("username", String.class);
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

    //클레임 가져오기
    private static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder().setSigningKey(getKey(key))
                .build().parseClaimsJws(token).getBody();
    }

    //토큰에 유저네임을 넣어서 유저가 어떤 유저인지 식별 가능
    //key 는 유저네임을 넣은 값을 암호화할때 사용
    public static String generateToken(String username, String key, long expiredTimeMs) {
        Date date = new Date(System.currentTimeMillis());
        log.info("토큰 발급 시간={}", date.toString());
        log.info("토큰 만료 시간={}", new Date(System.currentTimeMillis() + expiredTimeMs));

        Claims claims = Jwts.claims();
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key), SignatureAlgorithm.HS256) //256바이트 사용하는 해시알고리즘
                .compact(); //토큰 만드는 메서드
    }

    //내가 yml 에 등록한 secretKey 를 base64 을 통해 encode 하지 않고 그대로 사용한다.
    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
