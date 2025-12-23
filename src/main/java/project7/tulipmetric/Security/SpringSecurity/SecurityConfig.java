package project7.tulipmetric.Security.SpringSecurity;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // (선택) 개발 중이면 편의상 끄는 경우도 있음. 운영에선 켜는 걸 권장.
                // .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 회원가입/로그인 페이지 및 회원가입 처리 URL 허용
                        .requestMatchers(HttpMethod.GET,  "/join", "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/join", "/signup").permitAll()

                        // OAuth2 관련 엔드포인트 허용(리다이렉트/콜백 흐름에 필요)
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                                .anyRequest().permitAll() //테스트용

                        // 나머지는 인증 필요
//                        .anyRequest().authenticated()
                )

                // 폼 로그인: login.html 에서 POST로 넘어오는 걸 처리
                .formLogin(form -> form
                        .loginPage("/login")              // GET /login -> login.html
                        .loginProcessingUrl("/login")     // POST /login -> UsernamePasswordAuthenticationFilter
                        .usernameParameter("loginid")    // 폼 input name="loginid"
                        .passwordParameter("password")    // 폼 input name="password"
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // 소셜 로그인(OAuth2): /oauth2/authorization/{registrationId} 로 시작
//                .oauth2Login(oauth -> oauth
//                                .loginPage("/login")              // 인증 필요 시 login.html로 보냄(여기서 소셜 버튼 제공)
//                                .defaultSuccessUrl("/", true)
//                                .failureUrl("/login?error")
//                        // 필요하면 여기서 커스텀 서비스/핸들러 연결
//                        // .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
//                        // .successHandler(customOAuth2SuccessHandler)
//                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

                // (선택) remember-me, httpBasic 등 필요하면 추가
//                .httpBasic(Customizer.withDefaults());

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
        String roleHierarchyStringRepresentation = "ROLE_ADMIN > ROLE_MANAGER\n" +
                "ROLE_MANAGER > ROLE_USER";

        // 정적 팩토리 메서드 fromHierarchy()를 사용하여 RoleHierarchyImpl 인스턴스를 생성.
        return RoleHierarchyImpl.fromHierarchy(roleHierarchyStringRepresentation);
    }
}
