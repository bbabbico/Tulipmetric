package project7.tulipmetric.Security.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void Join(JoinDto dto) {
        String encodedPw = passwordEncoder.encode(dto.getPassword());
        try {
            Member member = new Member(
                    null,
                    dto.getEmail(),
                    dto.getLoginid(),
                    dto.getNickname(),
                    encodedPw,
                    Role.USER,
                    Join_type.FORM   // 예시: 폼 가입이면 이렇게 지정
            );
            memberRepository.save(member);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("회원가입완료");
    }

    // 회원가입 정보 중복확인 메서드
    public boolean LoginIdDuplicateCheck(String loginid) {
        return memberRepository.findByLoginid(loginid) == null;
    }

    public boolean NickNameDuplicateCheck(String nickname) {
        return memberRepository.findByNickname(nickname) == null;
    }
}
