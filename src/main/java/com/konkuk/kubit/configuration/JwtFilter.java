package com.konkuk.kubit.configuration;

import com.konkuk.kubit.service.UserService;
import com.konkuk.kubit.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final String secretKey;

    // post 요청에 대하여 jwt 인증을 거치도록 함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        logger.info("authentication : " + authorization);
        // 토큰 안보내면 block
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.error("authentication is null!!");
            filterChain.doFilter(request, response);
            return;
        }
        // extract token
        String token = authorization.split(" ")[1]; // Bearer token...
//        // check token is expired
//        if(JwtTokenUtil.isExpired(token, secretKey)){
//            logger.error("Token is expired");
//            filterChain.doFilter(request, response);
//            return;
//        }

        // extract payload from token
        String userId = "";
        try{
            userId = JwtTokenUtil.getUserIdFromToken(token, secretKey);
        }catch (ExpiredJwtException e){
            logger.error("token is expired");
            throw new JwtException("토큰 만료");
        }
        //check userId is valid
        logger.info("userId : "+userId);
        // authorization
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(("USER"))));
        // put detail
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // filter 수행
        filterChain.doFilter(request, response);
    }
}
