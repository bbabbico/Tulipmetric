package project7.tulipmetric.Security.SpringSecurity.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && tokenProvider.validate(token)) {
            var signedClaims = tokenProvider.parseSignedClaims(token);
            var claims = signedClaims.getPayload();

            String username = claims.getSubject();
            Object rolesClaim = claims.get("roles");
            String roles = rolesClaim == null ? "" : rolesClaim.toString();

            Instant issuedAt = claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant();
            Instant expiresAt = claims.getExpiration() == null ? null : claims.getExpiration().toInstant();

            Map<String, Object> jwtClaims = new HashMap<>();
            claims.forEach(jwtClaims::put);

            Map<String, Object> headers = new HashMap<>();
            signedClaims.getHeader().forEach(headers::put);

            Jwt jwt = new Jwt(token, issuedAt, expiresAt, headers, jwtClaims);

            List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .filter(StringUtils::hasText)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var auth = new JwtAuthenticationToken(jwt, authorities, username);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // 1) Authorization: Bearer xxx
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // 2) 쿠키 ACCESS_TOKEN
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
