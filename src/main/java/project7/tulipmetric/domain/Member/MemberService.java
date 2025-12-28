package project7.tulipmetric.domain.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public String NicknameFindByJwt(Jwt jwt){
        return memberRepository.findByNickname(jwt.getSubject()).getNickname();
    }

    public String FindByJwtLoginId(Jwt jwt){
        return memberRepository.findByLoginid(jwt.getSubject()).getLoginid();
    }

    public Member FindByLoginIdMember(String loginid){
        return memberRepository.findByLoginid(loginid);
    }
}
