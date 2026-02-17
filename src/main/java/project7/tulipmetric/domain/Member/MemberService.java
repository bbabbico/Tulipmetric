package project7.tulipmetric.domain.Member;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberService {
    // JWT -> 닉네임
    public Optional<String> findNicknameByJwt(Jwt jwt);

    // JWT -> Role
    public Optional<Role> findRoleByJwt(Jwt jwt);

    // JWT -> Member
    public Optional<Member> findMemberByJwt(Jwt jwt);

    // Loginid -> Member
    public Optional<Member> findByLoginId(String loginid);

    // Email ->Member
    public Optional<Member> findByEmail(String email);

    // Nickname -> Member
    public Optional<Member> findByNickname(String nick_name);

    // 저장
    public Member save(Member member);

    // 수정 nickname , email
    public Member updateProfile(Jwt jwt, String nickname, String email);

    //수정 Password
    public void updatePassword(Jwt jwt, String currentPassword, String newPassword);

    // JWT -> 회원탈퇴
    public void deleteByJwt(Jwt jwt);

//    PersonClaims findByLoginid(String subject);
}
