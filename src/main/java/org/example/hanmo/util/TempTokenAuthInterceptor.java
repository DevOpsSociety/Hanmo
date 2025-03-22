package org.example.hanmo.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.vaildate.AuthValidate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TempTokenAuthInterceptor implements HandlerInterceptor {

    private final AuthValidate authValidate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("tempToken")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다");
            return false;
        }
        String token = authHeader.substring(7);
        try {
            authValidate.validateTempToken(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 유효하지 않습니다");
            return false;
        }
        return true;
    }
}
