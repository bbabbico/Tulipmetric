package project7.tulipmetric.Security.SpringSecurity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project7.tulipmetric.Security.SpringSecurity.JWT.*;
import project7.tulipmetric.Security.SpringSecurity.OAuth2.CookieOAuth2AuthorizationRequestRepository;
import project7.tulipmetric.Security.SpringSecurity.OAuth2.CustomOAuth2UserService;
import project7.tulipmetric.Security.SpringSecurity.OAuth2.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-ms:1800000}") // 없으면 기본값 30분
    private long accessTokenMs;

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret, accessTokenMs);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider provider) {
        return new JwtAuthenticationFilter(provider);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(JwtTokenProvider provider) {
        return new OAuth2SuccessHandler(provider);
    }

    @Bean
    public CookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository() {
        return new CookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2SuccessHandler oAuth2SuccessHandler,
            CookieOAuth2AuthorizationRequestRepository cookieRepo,
            CustomOAuth2UserService customOAuth2UserService
    ) throws Exception {

        // 폼 로그인 JWT 발급 필터
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager, tokenProvider);

        http
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 완전 비활성(STATELESS)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                                .requestMatchers(HttpMethod.GET, "/join", "/login","/loginidcheck","/nicknamecheck").permitAll()
                                .requestMatchers(HttpMethod.POST, "/join", "/signup", "/login").permitAll()
                                .requestMatchers("/mypage","/activity","/accountsettings","/saved").authenticated() //마이 페이지
                                .requestMatchers("/createpost","/deletepost","/editpost").authenticated() //Post , Comment , Creat/Delete/Edit
                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

//                                .anyRequest().authenticated() // 운영 기준
                         .anyRequest().permitAll()    // 테스트
                )

                // 기본 formLogin 비활성 (JwtLoginFilter로 대체)
                .formLogin(AbstractHttpConfigurer::disable)

                // OAuth2 로그인: 성공 시 JWT 발급
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .authorizationEndpoint(a -> a.authorizationRequestRepository(cookieRepo))
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler((req, res, ex) -> res.sendRedirect("/login?error"))
                )

                // JWT 검증 필터(매 요청)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 로그인 처리 필터(POST /login)
                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)

                // 로그아웃: 세션이 없으니 "쿠키 삭제" 정도만 의미 있음
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((req, res, auth) -> {
                            var cookie = new jakarta.servlet.http.Cookie("ACCESS_TOKEN", "");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            cookie.setHttpOnly(true);
                            cookie.setSecure(true);
                            res.addCookie(cookie);
                            res.setStatus(200);
                            res.sendRedirect("/");
                        })
                )

                // 인증 실패시 401 내려주기(페이지가 아니라 API면 특히 중요)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")) //로그인 안했는데 로그인 필요한 페이지 접근하면 = 로그인 페이지로 이동
                        .accessDeniedHandler((req, res, ex) -> res.sendError(403))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierarchy(){
        // "ROLE_ADMIN > ROLE_MANAGER"는 ROLE_ADMIN이 ROLE_MANAGER의 권한을 포함함을 의미.
        // 줄 바꿈 문자(\n)를 사용하여 여러 계층을 정의가능.
        String roleHierarchyStringRepresentation = "ADMIN > USER\n" +
                "ROLE_MANAGER > ROLE_USER";

        // 정적 팩토리 메서드 fromHierarchy()를 사용하여 RoleHierarchyImpl 인스턴스를 생성.
        return RoleHierarchyImpl.fromHierarchy(roleHierarchyStringRepresentation);
    }
}
