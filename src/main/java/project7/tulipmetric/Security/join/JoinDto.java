package project7.tulipmetric.Security.join;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import project7.tulipmetric.domain.Member.Role;

import java.util.Objects;

@Setter
@Getter
@RequiredArgsConstructor
public final class JoinDto {
    private final String email;
    private final String loginid;
    private final String nickname;
    private final String password;
    private final Role role;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JoinDto) obj;
        return Objects.equals(this.email, that.email) &&
                Objects.equals(this.loginid, that.loginid) &&
                Objects.equals(this.nickname, that.nickname) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, loginid, nickname, password, role);
    }

    @Override
    public String toString() {
        return "JoinDto[" +
                "email=" + email + ", " +
                "loginid=" + loginid + ", " +
                "nickname=" + nickname + ", " +
                "password=" + password + ", " +
                "role=" + role + ']';
    }


}
