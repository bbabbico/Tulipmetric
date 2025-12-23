package project7.tulipmetric.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByLoginid(String login_id);
    Member findByEmail(String email);
    Member findByNickname(String nick_name);
}
