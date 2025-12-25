package project7.tulipmetric.Security.SpringSecurity.OAuth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.Join_type;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.MemberRepository;
import project7.tulipmetric.domain.Member.Role;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        SocialProfile profile = extractProfile(registrationId, oAuth2User.getAttributes());
        Member member = findOrCreateMember(profile);

        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole()));

        return new DefaultOAuth2User(authorities,
                Map.of(
                        "loginid", member.getLoginid(),
                        "email", member.getEmail(),
                        "nickname", member.getNickname()
                ),
                "loginid");
    }

    private Member findOrCreateMember(SocialProfile profile) {
        if (profile.email() == null || profile.email().isBlank()) {
            throw new OAuth2AuthenticationException("Email not provided by provider");
        }
        Member existing = memberRepository.findByEmail(profile.email());
        if (existing != null) {
            return existing;
        }

        String encodedPassword = "social"+UUID.randomUUID().toString();
        Date nowDate = new Date();
        SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        Member member = new Member(
                null,
                profile.email(),
                profile.loginId(),
                profile.nickname(),
                encodedPassword,
                simpleDateFormat.format(nowDate),
                Role.USER,
                profile.joinType()
        );
        log.info(member.toString());
        return memberRepository.save(member);
    }

    private SocialProfile extractProfile(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> fromGoogle(attributes);
            case "naver" -> fromNaver(attributes);
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        };
    }

    private SocialProfile fromGoogle(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.getOrDefault("name", email);
        String sub = String.valueOf(attributes.get("sub"));
        return new SocialProfile(email, "google_" + sub, name, Join_type.GOOGLE);
    }

    private SocialProfile fromNaver(Map<String, Object> attributes) {
        Map<String, Object> response = attributes.get("response") instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");
        String id = String.valueOf(response.get("id"));
        return new SocialProfile(email, "naver_" + id, nickname, Join_type.NAVER);
    }

    private record SocialProfile(String email, String loginId, String nickname, Join_type joinType) {
    }
}
