package project7.tulipmetric.domain.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.Role;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<String> NicknameFindByJwt(Jwt jwt){
        return findMemberByJwt(jwt).map(Member::getNickname);
    }

    public Optional<String> FindByJwtLoginId(Jwt jwt){
        return findMemberByJwt(jwt).map(Member::getLoginid);
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

    private Optional<Member> findMemberByJwt(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(memberRepository.findByLoginid(jwt.getSubject()));
    }
}
