package project7.tulipmetric.Mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
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
    private final MypageService mypageService;

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal Jwt jwt , Model model) {
        Member member = memberService.findByLoginId(jwt.getSubject())
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자만 접근할 수 있습니다."));
        log.info("회원인증 완료 {}", member.toString());

        log.info("{}",likeService.findAllByLoginid(member.getLoginid()).size());
        model.addAttribute("likecount",likeService.findAllByLoginid(member.getLoginid()).size());
        model.addAttribute("commentcount",commentService.countByJwt(jwt));
        model.addAttribute("postcount",postService.findAllByNickname(member.getNickname()).size());
        model.addAttribute("member", member);
        return "Mypage/mypage";
    }

    @GetMapping("/accountsettings")
    public String accountSettings(@AuthenticationPrincipal Jwt jwt,Model model){
        model.addAttribute("member",memberService.findMemberByJwt(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.")));
        return "Mypage/account-settings";
    }

    @GetMapping("/activity")
    public String activity(@AuthenticationPrincipal Jwt jwt,Model model){
        model.addAttribute("likes",mypageService.LoadPostsByLike(jwt));
        model.addAttribute("posts",mypageService.LoadPostsByNickname(jwt));
        model.addAttribute("comments",mypageService.LoadPostsByComment(jwt));

        model.addAttribute("commentcount",commentService.countByJwt(jwt));
        return "Mypage/activity";
    }

    @PostMapping("/editprofile")
    public String editProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            Model model
    ) {
        memberService.updateProfile(jwt, nickname, email);
        model.addAttribute("member", memberService.findMemberByJwt(jwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.")));
        return "redirect:/accountsettings";
    }

    @PostMapping("/editpassword")
    public String editPassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String currentPassword,
            @RequestParam String newPassword
    ) {
        memberService.updatePassword(jwt, currentPassword, newPassword);
        return "redirect:/accountsettings";
    }

    @PostMapping("/deletprofile")
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal Jwt jwt) {
        memberService.deleteByJwt(jwt);
        return ResponseEntity.noContent().build();
    }
}
