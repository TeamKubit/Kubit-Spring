package com.konkuk.kubit.configuration;

import com.konkuk.kubit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 모든 api에 auth 정보를 요구하도록 함
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    @Value("${jwt.token.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable() //ui disabled
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests()
//                .antMatchers("/api/**").permitAll()
                .antMatchers("/api/v1/user/join/", "/api/v1/user/login/", "/api/v1/user/refresh/").permitAll() //로그인과 회원가입은 허용해야함 + 갱신
                .antMatchers(HttpMethod.POST).authenticated() // 다른 POST 요청은 검증 필요
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //jwt 사용하는 경우
                .and()
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class) //jwt filter 인증 -> username password 인증
                .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class) // JwtFilter에서 에러발생한 경우 ExceptionFilter에서 처리
                .build();
    }
}
