package project7.tulipmetric.Security.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import project7.tulipmetric.Security.join.JoinDto;
import project7.tulipmetric.Security.join.JoinService;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @GetMapping("/join")
    public String Getjoin() {
        return "/Login/join";
    }

    @PostMapping("/join")
    public String Postjoin(JoinDto joinDto) {
        joinService.Join(joinDto);
        return "/Login/login";
    }

    @ResponseBody
    @PostMapping("/loginidcheck")
    public ResponseEntity<Integer> Postloginidcheck(String loginid) {
        if (joinService.LoginIdDuplicateCheck(loginid)) {return new ResponseEntity<>(1, HttpStatus.OK);
        } else {return new ResponseEntity<>(0, HttpStatus.OK);}
    }

    @ResponseBody
    @PostMapping("/nicknamecheck")
    public ResponseEntity<Integer> Postnicknamecheck(String nickname) {
        if (joinService.NickNameDuplicateCheck(nickname)) {return new ResponseEntity<>(1, HttpStatus.OK);
        } else {return new ResponseEntity<>(0, HttpStatus.OK);}
    }


}
