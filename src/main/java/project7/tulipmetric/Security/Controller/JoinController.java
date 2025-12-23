package project7.tulipmetric.Security.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public String Postjoin(@RequestBody JoinDto joinDto) {
        joinService.Join(joinDto);
        return "/Login/join";
    }
}
