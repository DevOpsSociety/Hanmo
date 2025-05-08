package org.example.hanmo.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.hanmo.vaildate.AdminValidate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthCheckAspect {

    private final AdminValidate adminValidate;

    public AuthCheckAspect(AdminValidate adminValidate) {
        this.adminValidate = adminValidate;
    }

    @Around("@annotation(org.example.hanmo.aop.AdminCheck)")
    public Object checkAdmin(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attrs.getRequest();
        String tempToken = req.getHeader("tempToken");

        adminValidate.verifyAdmin(tempToken);
        return pjp.proceed();
    }
}
