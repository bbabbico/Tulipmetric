package project7.tulipmetric.Mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {

    @GetMapping("/mypage")
    public String mypage(){
        return "/Mypage/mypage";
    }

    @GetMapping("/accountsettings")
    public String accountSettings(){
        return "/Mypage/account-settings";
    }

    @GetMapping("/activity")
    public String activity(){
        return "/Mypage/activity";
    }

    @GetMapping("/saved")
    public String saved(){
        return "/Mypage/saved";
    }
}
