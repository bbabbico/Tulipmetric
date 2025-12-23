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
        Member member = memberRepository.findByLoginid(Loginid);
        return new MemberUserDetails(member);
    }
}
