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
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void Join(JoinDto dto) {
        String encodedPw = passwordEncoder.encode(dto.getPassword());
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        try {
            MemberEntity memberEntity = new MemberEntity(
                    null,
                    dto.getEmail(),
                    dto.getLoginid(),
                    dto.getNickname(),
                    encodedPw,
                    simpleDateFormat.format(nowDate),
                    Role.USER,
                    Join_type.FORM   // 예시: 폼 가입이면 이렇게 지정
            );
            memberRepository.save(memberEntity);
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
