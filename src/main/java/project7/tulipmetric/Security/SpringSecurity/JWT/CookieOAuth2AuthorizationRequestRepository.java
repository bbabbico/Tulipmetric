package project7.tulipmetric.Security.SpringSecurity.JWT;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String COOKIE_NAME = "OAUTH2_AUTH_REQUEST";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String value = getCookie(request, COOKIE_NAME);
        if (value == null) return null;
        byte[] decoded = Base64.getUrlDecoder().decode(value);
        // 간단화를 위해 직렬화/역직렬화는 생략하면 안 됨.
        // 실무에서는 SerializationUtils 또는 JSON 직렬화로 OAuth2AuthorizationRequest를 저장/복구 구현이 필요.
        // 여기서는 "구조가 필요하다"는 점만 보여주는 뼈대 예시.
        return null;
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            deleteCookie(response, COOKIE_NAME);
            return;
        }
        // 마찬가지로 authorizationRequest를 안전하게 직렬화해서 쿠키에 저장해야 함
        String dummy = Base64.getUrlEncoder().encodeToString("DUMMY".getBytes(UTF_8));
        addCookie(response, COOKIE_NAME, dummy, 180);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(response, COOKIE_NAME);
        return null;
    }

    private String getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
