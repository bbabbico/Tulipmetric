package project7.tulipmetric.MainService.Post.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import project7.tulipmetric.MainService.Post.Service.CreateCommentService;
import project7.tulipmetric.domain.Member.MemberRepository;
import project7.tulipmetric.domain.Member.Role;
import project7.tulipmetric.domain.Post.Comment;
import project7.tulipmetric.domain.Post.CommentDto;
import project7.tulipmetric.domain.Post.Post;
import project7.tulipmetric.domain.Post.PostDto;
import project7.tulipmetric.MainService.Post.Service.CreatePostService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final MemberRepository memberRepository;
    private final CreatePostService createPostService;
    private final CreateCommentService createCommentService;

    @GetMapping("/community")
    public String community(){
        return "/MainService/community/community";
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

        createCommentService.SavePost(new Comment(null,createPostService.FindByPostId(commentDto.postid()),nickname,commentDto.content(),commentDto.dateminute(),commentDto.likenum()));

        return "redirect:/community";
    }

}
