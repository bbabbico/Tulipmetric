package project7.tulipmetric.Security.SpringSecurity.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.MemberEntity;
import project7.tulipmetric.domain.Member.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService { //시큐리티 로그인 과정에서 UserDetailsService 를 정의.

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String Loginid) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByLoginid(Loginid);
        if (memberEntity == null) {
            log.info("Loginid:{} 는 없는 회원임.",Loginid);
            throw new UsernameNotFoundException(Loginid);
        }

        log.info("MemberEntity :{}", memberEntity.toString());
        return new MemberUserDetails(memberEntity);
    }
}
