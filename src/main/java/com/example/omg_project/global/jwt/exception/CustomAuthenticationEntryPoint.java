package com.example.omg_project.global.jwt.exception;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * 시큐리티가 인증되지 않은 사용자가 보호된 리소스에 접근할 때 동작하는 클래스
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String)request.getAttribute("exception");

        if(isRestRequest(request)){
            handleRestResponse(request, response, exception);
        } else {
            handlePageResponse(request, response, exception);
        }
    }

    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        String alertMessage = "로그인 해주세요";

        if (exception != null) {
            switch (exception) {
                case "INVALID_TOKEN":
                    alertMessage = "유효하지 않은 토큰입니다. 다시 로그인해주세요.";
                    break;
                case "EXPIRED_TOKEN":
                    alertMessage = "토큰이 만료되었습니다. 다시 로그인해주세요.";
                    break;
                case "UNSUPPORTED_TOKEN":
                    alertMessage = "지원되지 않는 토큰입니다. 다시 로그인해주세요.";
                    break;
                case "NOT_FOUND_TOKEN":
                    alertMessage = "로그인 후 이용해주세요";
                    break;
                default:
                    alertMessage = "알 수 없는 오류가 발생했습니다. 다시 시도해주세요.";
                    break;
            }
        }

        // 알림창 표시 후 메인 페이지로 리다이렉트하는 자바스크립트 코드
        String script = "<script type='text/javascript'>"
                + "alert('" + alertMessage + "');"
                + "window.location.href='/';"
                + "</script>";

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(script);
    }

    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI().startsWith("/api/");
    }

    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {

        if (exception != null) {
            log.error("Rest Request - Commence Get Exception : {}", exception);
            if (exception.equals(JwtExceptionCode.INVALID_TOKEN.getCode())) {
                log.error("entry point >> invalid token");
                setResponse(response, JwtExceptionCode.INVALID_TOKEN);
            } else if (exception.equals(JwtExceptionCode.EXPIRED_TOKEN.getCode())) {
                log.error("entry point >> expired token");
                setResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.UNSUPPORTED_TOKEN.getCode())) {
                log.error("entry point >> unsupported token");
                setResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.NOT_FOUND_TOKEN.getCode())) {
                log.error("entry point >> not found token");
                setResponse(response, JwtExceptionCode.NOT_FOUND_TOKEN);
            } else {
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
        } else {
            setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
        }
        response.sendRedirect("/signin");
    }

    private void setResponse(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        response.getWriter().print(responseJson);
    }
}