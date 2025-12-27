package project7.tulipmetric.MainService.Post.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project7.tulipmetric.MainService.Post.Service.CreateCommentService;
import project7.tulipmetric.domain.Member.MemberRepository;
import project7.tulipmetric.domain.Member.Role;
import project7.tulipmetric.domain.Post.*;
import project7.tulipmetric.MainService.Post.Service.CreatePostService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CreatePostService createPostService;
    private final CreateCommentService createCommentService;

    @GetMapping("/community")
    public String community(Model model){
        model.addAttribute("posts",postRepository.findAll());
        return "/MainService/community/community";
    }

    @GetMapping("/discussion-detail")
    public String discussion_detail(@RequestParam Long id, Model model){
        Post post = postRepository.findById(id).get();
        model.addAttribute("post",post);
        model.addAttribute("comments",commentRepository.findAllByPostid(post));
        return "/MainService/community/discussion-detail";
    }

    @GetMapping("/createpost")
    public String createpost(){
        return "/MainService/community/createpost";
    }

    @PostMapping("/createpost")
    public String SavePost(@AuthenticationPrincipal Jwt jwt, PostDto postDto){
        String nickname= memberRepository.findByLoginid(jwt.getSubject()).getNickname();
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        createPostService.SavePost(new Post(null,nickname,postDto.category(),postDto.industryTag(),postDto.title(),postDto.content(),simpleDateFormat.format(nowDate),0,0, Role.USER));

        return "redirect:/community";
    }

    @PostMapping("/createcomment")
    public String SaveComment(@AuthenticationPrincipal Jwt jwt, CommentDto commentDto){
        String nickname= memberRepository.findByLoginid(jwt.getSubject()).getNickname();
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        createCommentService.SavePost(new Comment(null,createPostService.FindByPostId(commentDto.postid()),nickname,commentDto.content(),simpleDateFormat.format(nowDate),0));

        return "redirect:/community";
    }

}
