package project7.tulipmetric.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByLoginid(String login_id);
    MemberEntity findByEmail(String email);
    MemberEntity findByNickname(String nick_name);
}
