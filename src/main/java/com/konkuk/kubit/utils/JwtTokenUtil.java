package com.konkuk.kubit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {
    public static String createToken(String userId, String key, long expireTimeMs) {
        //static으로 갖다 쓸 수 있도록 함
        Claims claims = Jwts.claims(); //map for jwt data
        claims.put("userId", userId); //userId를 암호화 하여 토큰으로 제공될 예정
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }
}
