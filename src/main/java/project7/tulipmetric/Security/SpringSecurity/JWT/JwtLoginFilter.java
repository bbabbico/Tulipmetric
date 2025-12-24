package project7.tulipmetric.Security.SpringSecurity.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;

        // 기존과 동일하게 POST /login 처리
        setUsernameParameter("loginid");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // 1) x-www-form-urlencoded (기존 폼 전송)
        String loginId = obtainUsername(request);
        String password = obtainPassword(request);

        // 2) 만약 JSON으로도 받고 싶다면:
        // Content-Type이 JSON이면 바디 파싱해서 loginid/password 읽는 로직을 추가할 수 있음.

        var authRequest = new UsernamePasswordAuthenticationToken(loginId, password);
        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {

        String accessToken = tokenProvider.createAccessToken(authResult);

        // (권장) HttpOnly 쿠키로 내려주기 (Thymeleaf 페이지도 그대로 사용 가능)
        Cookie cookie = new Cookie("ACCESS_TOKEN", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);      // https 환경에서 true 권장
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30);   // 30분 예시
        // SameSite는 서블릿 Cookie 표준 API로 직접 설정이 까다로워서 보통 response header로 세팅하거나 톰캣 설정 사용

        response.addCookie(cookie);

        response.sendRedirect("/");

        // JSON 응답도 같이 주기(원하면 프론트에서 토큰 저장 가능)
//        response.setStatus(200);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        objectMapper.writeValue(response.getWriter(), Map.of(
//                "accessToken", accessToken,
//                "tokenType", "Bearer"
//        ));
    }

    @Override
    protected void unsuccessfulAuthentication( //로그인 오류시 리다이렉트 없이 메시지만 전송
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failed
    ) throws IOException {
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "error", "LOGIN_FAILED",
                "message", failed.getMessage()
        ));
    }
}
