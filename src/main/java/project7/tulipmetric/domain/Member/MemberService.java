package project7.tulipmetric.domain.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<String> findNicknameByJwt(Jwt jwt) {
        return findMemberByJwt(jwt).map(MemberEntity::getNickname);
    }

    public Optional<MemberEntity> findByLoginId(String loginid) {
        if (loginid == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(memberRepository.findByLoginid(loginid));
    }

    public Optional<Role> findRoleByJwt(Jwt jwt) {
        return findMemberByJwt(jwt).map(MemberEntity::getRole);
    }

    public Optional<MemberEntity> findMemberByJwt(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(memberRepository.findByLoginid(jwt.getSubject()));
    }

    @Transactional
    public MemberEntity updateProfile(Jwt jwt, String nickname, String email) {
        MemberEntity memberEntity = findMemberByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (StringUtils.hasText(nickname)) {
            memberEntity.updateNickname(nickname);
        }

        if (StringUtils.hasText(email)) {
            memberEntity.updateEmail(email);
        }

        return memberEntity;
    }

    @Transactional
    public void updatePassword(Jwt jwt, String currentPassword, String newPassword) {
        MemberEntity memberEntity = findMemberByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(currentPassword, memberEntity.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        memberEntity.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deleteByJwt(Jwt jwt) {
        MemberEntity memberEntity = findMemberByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberRepository.delete(memberEntity);
    }
}
