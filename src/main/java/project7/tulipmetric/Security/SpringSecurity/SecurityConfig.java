package project7.tulipmetric.Security.SpringSecurity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project7.tulipmetric.Security.SpringSecurity.JWT.*;
import project7.tulipmetric.Security.SpringSecurity.OAuth2.CookieOAuth2AuthorizationRequestRepository;
import project7.tulipmetric.Security.SpringSecurity.OAuth2.CustomOAuth2UserService;
import project7.tulipmetric.Security.SpringSecurity.OAuth2.OAuth2SuccessHandler;
import project7.tulipmetric.domain.Member.Role;

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

        // 권한 묶음
        String[] userAndLootRoles = {Role.USER.name(), Role.LOOT.name()};

        http
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 완전 비활성(STATELESS)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 기본 formLogin 비활성 (JwtLoginFilter로 대체)
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/img/**").permitAll() //정적 이미지
                                .requestMatchers(HttpMethod.GET, "/join", "/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/join", "/signup", "/login","/nicknamecheck","/loginidcheck").permitAll()
                                .requestMatchers("/","/industry-detail","/industry-detail/**").permitAll()
                                .requestMatchers("/mypage","/activity","/accountsettings","/saved").hasAnyRole(userAndLootRoles) //마이 페이지
                                .requestMatchers("/editprofile","/editpassword","/deletprofile").hasAnyRole(userAndLootRoles) //마이 페이지 - 사용자 정보 수정
                                .requestMatchers("/createpost","/deletepost","/editpost").hasAnyRole(userAndLootRoles) //Post , Comment , Creat/Delete/Edit
                                .requestMatchers("/createcomment","/deletecomment","/editcomment").hasAnyRole(userAndLootRoles)
                                .requestMatchers(HttpMethod.POST, "/likeAction", "/unlikeAction").hasAnyRole(userAndLootRoles)
                                .requestMatchers(HttpMethod.POST, "/savewishmarket", "/deletwishmarket").hasAnyRole(userAndLootRoles)
                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                                .anyRequest().authenticated() // 운영 기준
//                         .anyRequest().permitAll()    // 테스트
                )


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

                // 로그아웃 - 쿠키 제거
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
                        .accessDeniedHandler((req, res, ex) -> res.sendError(403,"해당 권한으로 접근할 수 없는 페이지 입니다.")) //로그인 후, 권한 밖의 페이지를 접속한경우 에러응답
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierarchy(){
        // "ROLE_LOOT > ROLE_USER"는 ROLE_LOOT이 ROLE_USER의 권한을 포함함을 의미.
        // 줄 바꿈 문자(\n)를 사용하여 여러 계층을 정의가능.
        String roleHierarchyStringRepresentation = "ROLE_LOOT > ROLE_USER";

        // 정적 팩토리 메서드 fromHierarchy()를 사용하여 RoleHierarchyImpl 인스턴스를 생성.
        return RoleHierarchyImpl.fromHierarchy(roleHierarchyStringRepresentation);
    }
}
