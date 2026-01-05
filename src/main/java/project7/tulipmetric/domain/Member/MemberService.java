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

    public Optional<String> NicknameFindByJwt(Jwt jwt){
        return findMemberByJwt(jwt).map(Member::getNickname);
    }

    public Optional<Member> FindByLoginIdMember(String loginid){
        if (loginid == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(memberRepository.findByLoginid(loginid));
    }

    public Optional<Role> RoleFindByJwt(Jwt jwt) {
        return findMemberByJwt(jwt).map(Member::getRole);
    }

    public Optional<Member> findMemberByJwt(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(memberRepository.findByLoginid(jwt.getSubject()));
    }

    @Transactional
    public Member updateProfile(Jwt jwt, String nickname, String email) {
        Member member = findMemberByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (StringUtils.hasText(nickname)) {
            member.updateNickname(nickname);
        }

        if (StringUtils.hasText(email)) {
            member.updateEmail(email);
        }

        return member;
    }

    @Transactional
    public void updatePassword(Jwt jwt, String currentPassword, String newPassword) {
        Member member = findMemberByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deleteByJwt(Jwt jwt) {
        Member member = findMemberByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberRepository.delete(member);
    }
}
