package project7.tulipmetric.Mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project7.tulipmetric.MainService.Community.Service.CommentService;
import project7.tulipmetric.MainService.Community.Service.LikeService;
import project7.tulipmetric.MainService.Community.Service.PostService;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal Jwt jwt , Model model) {
        Member member = memberService.FindByLoginIdMember(jwt.getSubject());
        log.info("회원인증 완료 {}",member.toString());
        log.info("{}",likeService.findAllByLoginid(member.getLoginid()).size());
        model.addAttribute("likecount",likeService.findAllByLoginid(member.getLoginid()).size());
        model.addAttribute("commentcount",commentService.FindAllByNickname(member.getNickname()).size());
        model.addAttribute("postcount",postService.FindAllByNickname(member.getNickname()).size());
        model.addAttribute("member", member);
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
