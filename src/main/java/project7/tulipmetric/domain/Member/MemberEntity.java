package project7.tulipmetric.domain.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueLoginid", columnNames = {"loginid"}),
        @UniqueConstraint(name = "UniqueNickname", columnNames = {"nickname"})
})
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //사용자 식별 번호

    @Column(length = 100 , nullable = false)
    private String email;    //이메일

    @Column(length = 100 , nullable = false)
    private String loginid;  //로그인 ID

    @Column(length = 20 , nullable = false)
    private String nickname;     //사용자 이름

    @Column(length = 61 , nullable = false) // BCryptPasswordEncoder
    private String password; //비밀번호

    @Column(nullable = false)
    private String creatdatetime; // SQL TIMESTAMP 타입 매핑

    @Enumerated(EnumType.STRING) //enum 값을 string 으로 저장
    @Column(length = 20 , nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private Join_type jointype; // 로그인 방식이 소셜 로그인 / 폼 방식인지 체크

    public MemberDto ToDomain() {
        return new MemberDto(email, loginid, nickname, role);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "MemberEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", loginid='" + loginid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", jointype=" + jointype +
                '}';
    }
}
