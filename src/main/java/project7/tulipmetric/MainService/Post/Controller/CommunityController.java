package project7.tulipmetric.MainService.Post.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project7.tulipmetric.MainService.Post.Service.CommentService;
import project7.tulipmetric.MainService.Post.Service.LikeService;
import project7.tulipmetric.domain.Member.MemberService;
import project7.tulipmetric.domain.Member.Role;
import project7.tulipmetric.MainService.Post.Service.PostService;
import project7.tulipmetric.domain.Post.Comment.Comment;
import project7.tulipmetric.domain.Post.Comment.CommentDto;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping("/community")
    public String community(Model model){
        model.addAttribute("posts",postService.FindAll());
        return "/MainService/community/community";
    }

    @GetMapping("/discussion-detail")
    public String discussion_detail(@RequestParam Long id,@AuthenticationPrincipal Jwt jwt ,Model model){
        Post post = postService.FindByPostId(id);
        List<Comment> comments = commentService.FindAllByPostid(post);
        Boolean Check = likeService.CheckLike(jwt,post);
        
        if (post.getNickname().equals(memberService.FindByJwtNickname(jwt))) { //작성자 본인인지 확인
            model.addAttribute("host",true);
        }

        model.addAttribute("check",Check); // 사용자 좋아요/북마크 여부
        model.addAttribute("post",post); //게시글 정보
        model.addAttribute("comments",comments); // 댓글 정보

        return "/MainService/community/discussion-detail";
    }

    @GetMapping("/createpost")
    public String createpost(){
        return "/MainService/community/createpost";
    }

    @PostMapping("/createpost")
    public String SavePost(@AuthenticationPrincipal Jwt jwt, PostDto postDto){
        String nickname= memberService.FindByJwtNickname(jwt);
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        postService.SavePost(new Post(null,nickname,postDto.category(),postDto.industryTag(),postDto.title(),postDto.content(),simpleDateFormat.format(nowDate),0,0, Role.USER));

        return "redirect:/community";
    }

    @GetMapping("/editpost") // 수정 페이지 Get
    public String GetEditPost(Model model,@RequestParam Long id){
        Post post = postService.FindByPostId(id);
        model.addAttribute("postDto",new PostDto(post.getCategory(),post.getIndustryTag(),post.getTitle(),post.getContent()));
        model.addAttribute("postid",id);

        return "/MainService/community/editpost";
    }

    @PostMapping("/editpost") // 수정 로직
    public String EditPost(@ModelAttribute PostDto postDto, @RequestParam Long id){
        Post post = postService.FindByPostId(id);
        postService.EditPost(post,postDto);
        return "redirect:/discussion-detail?id="+id;
    }

    @PostMapping("/deletepost")
    public String DeletePost(@RequestParam Long id){
        postService.DeletePost(id);
        return "redirect:/community";
    }

    @PostMapping("/createcomment")
    public String SaveComment(@AuthenticationPrincipal Jwt jwt, CommentDto commentDto){
        String nickname= memberService.FindByJwtNickname(jwt);
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        commentService.SaveComment(new Comment(null, postService.FindByPostId(commentDto.postid()),nickname,commentDto.content(),simpleDateFormat.format(nowDate),0));

        return "redirect:/discussion-detail?id="+commentDto.postid();
    }

    @ResponseBody
    @PostMapping("/likeAction")
    public ResponseEntity<Boolean> LikeAction(@RequestParam Long id, @AuthenticationPrincipal Jwt jwt){
        likeService.PostLikeAction(jwt,id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/unlikeAction")
    public ResponseEntity<Boolean> UnLikeAction(@RequestParam Long id, @AuthenticationPrincipal Jwt jwt){
        likeService.PostUnLikeAction(jwt,id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
