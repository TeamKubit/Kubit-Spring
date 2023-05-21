package com.konkuk.kubit.utils;

import com.konkuk.kubit.domain.dto.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    public static TokenInfo createToken(String userId, String key, long accessExpireTimeMs, long refreshExpireTimeMs) {
        //static으로 갖다 쓸 수 있도록 함
        Claims claims = Jwts.claims(); //map for jwt data
        claims.put("userId", userId); //userId를 암호화 하여 토큰으로 제공될 예정(payload)

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static String getUserIdFromToken(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userId", String.class);
    }

}
