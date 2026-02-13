package project7.tulipmetric.Security.SpringSecurity.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtTokenProvider {

    private final SecretKey key;
    private final MacAlgorithm alg;
    private final long accessTokenMs;

    /**
     * @param secret        보통 BASE64 인코딩된 시크릿을 권장.
     *                      (BASE64가 아니면 자동으로 UTF-8 bytes로 fallback)
     * @param accessTokenMs access token 만료(ms)
     */
    public JwtTokenProvider(String secret, long accessTokenMs) {
        this.alg = Jwts.SIG.HS256; // 최신 권장 상수 (HS384/HS512도 가능) :contentReference[oaicite:4]{index=4}
        this.key = toHmacKey(secret);
        this.accessTokenMs = accessTokenMs;
    }

    private SecretKey toHmacKey(String secret) {
        byte[] keyBytes;
        try {
            // secret을 BASE64로 관리하는 게 일반적
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException e) {
            // BASE64가 아니라면 UTF-8 bytes로 fallback
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        String username = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenMs);

        // setSubject/setIssuedAt/setExpiration 대신 subject/issuedAt/expiration
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key, alg) // signWith(Key, SecureDigestAlgorithm)
                .compact();
    }

    public Jws<Claims> parseSignedClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public boolean validate(String token) {
        try {
            parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 에서 유저 아이디, 권한 조회
    public String getUsername(String token) {
        return parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRoles(String token) {
        Object roles = parseSignedClaims(token).getPayload().get("roles");
        return roles == null ? "" : roles.toString();
    }
}
