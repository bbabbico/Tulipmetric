package project7.tulipmetric.Security.SpringSecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService { //시큐리티 로그인 과정에서 UserDetailsService 를 정의.

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String Loginid) throws UsernameNotFoundException {
        // TODO : 로그인 ID 를 잘못 입력했을떄 그냥 java.lang.NullPointerException 떠버림. 예외처리해야됨 로그인Id 는 맞는데 비밀번호 틀리면 SecurityConfig의 .failureUrl("/login?error") 이거 호출됨.
        Member member = memberRepository.findByLoginid(Loginid);
        return new MemberUserDetails(member);
    }
}
