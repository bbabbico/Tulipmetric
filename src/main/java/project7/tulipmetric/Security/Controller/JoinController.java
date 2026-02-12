package project7.tulipmetric.Security.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project7.tulipmetric.Security.join.CaptchaService;
import project7.tulipmetric.Security.join.JoinDto;
import project7.tulipmetric.Security.join.JoinService;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;
    private final CaptchaService captchaService;

    @GetMapping("/join")
    public String Getjoin() {
        return "Login/join";
    }

    @PostMapping("/join")
    public String Postjoin(
            JoinDto joinDto,
            @RequestParam(name = "g-recaptcha-response", required = false) String captchaToken
    ) {
        if (!captchaService.verify(captchaToken)) {
            return "redirect:/join?captchaError=1";
        }

        joinService.Join(joinDto);
        return "redirect:/login";
    }

    @ResponseBody
    @PostMapping("/loginidcheck")
    public ResponseEntity<Integer> Postloginidcheck(@RequestBody Map<String ,String> map) {
        if (joinService.LoginIdDuplicateCheck(map.get("loginid"))) {return new ResponseEntity<>(1, HttpStatus.OK);
        } else {return new ResponseEntity<>(0, HttpStatus.OK);}
    }

    @ResponseBody
    @PostMapping("/nicknamecheck")
    public ResponseEntity<Integer> Postnicknamecheck(@RequestBody Map<String ,String> map) {
        if (joinService.NickNameDuplicateCheck(map.get("nickname"))) {return new ResponseEntity<>(1, HttpStatus.OK);
        } else {return new ResponseEntity<>(0, HttpStatus.OK);}
    }


}
