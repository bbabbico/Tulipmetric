package project7.tulipmetric.Security.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinService {
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    public void Join(JoinDto dto) {
        String encodedPw = passwordEncoder.encode(dto.getPassword());
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        try {
            Member member = new Member(
                    null,
                    dto.getEmail(),
                    dto.getLoginid(),
                    dto.getNickname(),
                    encodedPw,
                    simpleDateFormat.format(nowDate),
                    Role.USER,
                    Join_type.FORM   // 예시: 폼 가입이면 이렇게 지정
            );
            memberService.save(member);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("회원가입완료");
    }

    // 회원가입 정보 중복확인 메서드
    public boolean LoginIdDuplicateCheck(String loginid) {
        return memberService.findByLoginId(loginid).isEmpty();
    }

    public boolean NickNameDuplicateCheck(String nickname) {
        return memberService.findByNickname(nickname).isEmpty();
    }
}
