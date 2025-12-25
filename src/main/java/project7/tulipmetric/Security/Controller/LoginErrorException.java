package project7.tulipmetric.Security.Controller;

import lombok.Getter;

// 회원 가입시 중복 회원가입을 방지 하기위한 예외
@Getter
public class LoginErrorException extends RuntimeException {

    public LoginErrorException(String message) {
        super(message);
    }

}