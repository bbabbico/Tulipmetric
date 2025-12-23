package project7.tulipmetric.domain.Member;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //사용자 식별 번호

    @Column(length = 100 , nullable = false)
    private String email;    //이메일

    @Column(length = 30 , nullable = false)
    private String login_id;  //로그인 ID

    @Column(length = 20 , nullable = false)
    private String nick_name;     //사용자 이름

    @Column(length = 61 , nullable = false) // BCryptPasswordEncoder
    private String password; //비밀번호

    @Enumerated(EnumType.STRING) //enum 값을 string 으로 저장
    @Column(length = 20 , nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private Join_type join_type; // 로그인 방식이 소셜 로그인 / 폼 방식인지 체크


    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login_id='" + login_id + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", join_type='" + join_type + '\'' +
                '}';
    }
}
