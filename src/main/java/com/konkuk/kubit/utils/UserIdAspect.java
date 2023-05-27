package com.konkuk.kubit.utils;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserIdAspect {
    @Autowired
    private UserRepository userRepository;

    @Around("execution(* *(.., @GetUser (*), ..))") //@GetUser를 파라미터로 하는 것에서 User가져올 수 있도록 함
    public Object testAop(ProceedingJoinPoint joinPoint) throws Throwable {
        //return user after check user is authorized
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOTFOUND, "해당하는 사용자가 없습니다"));
        Object result = joinPoint.proceed(new Object[]{user}); //user 정보 인자에 주입
        return result;
    }
}
