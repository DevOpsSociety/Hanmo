package org.example.hanmo.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.example.hanmo.vaildate.AdminValidate;
import org.example.hanmo.vaildate.AuthValidate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthCheckAspect {

    private final AuthValidate authValidate;     // 사용자 인증용
    private final AdminValidate adminValidate;   // 관리자 권한 검증용

    @Around("@annotation(org.example.hanmo.annotation.LoginRequired) || @within(org.example.hanmo.annotation.LoginRequired)")
    public Object aroundUserCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = resolveHttpServletRequest();
        String tempToken = resolveTempToken(request);
        authValidate.validateTempToken(tempToken);
        return joinPoint.proceed();
    }

    @Around("@annotation(org.example.hanmo.annotation.AdminCheck)")
    public Object aroundAdminCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = resolveHttpServletRequest();
        String tempToken = resolveTempToken(request);
        authValidate.validateTempToken(tempToken);
        adminValidate.verifyAdmin(tempToken);
        return joinPoint.proceed();
    }
    private HttpServletRequest resolveHttpServletRequest() {
        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("현재 요청 컨텍스트를 찾을 수 없습니다.");
        }
        return attrs.getRequest();
    }
    private String resolveTempToken(HttpServletRequest request) {
        String token = request.getHeader("tempToken");
        if (token == null || token.isBlank()) {
            token = request.getParameter("tempToken");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("tempToken이 존재하지 않습니다.");
        }
        return token;
    }
}
