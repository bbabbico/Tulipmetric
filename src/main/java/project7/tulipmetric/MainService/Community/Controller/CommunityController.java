package project7.tulipmetric.MainService.Community.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import project7.tulipmetric.MainService.Community.Service.CommentService;
import project7.tulipmetric.MainService.Community.Service.LikeService;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.MemberService;
import project7.tulipmetric.domain.Member.Role;
import project7.tulipmetric.MainService.Community.Service.PostService;
import project7.tulipmetric.domain.Post.Comment.Comment;
import project7.tulipmetric.domain.Post.Comment.CommentDto;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
// TODO : IsLoot 도매인 매서드로 쿼리 단축 필요
public class CommunityController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping("/community") // 커뮤니티 메인 페이지 GET
    public String community(Model model){
        model.addAttribute("posts",postService.findAll());
        return "MainService/community/community";
    }

    // TODO : 쿼리단축 완료
    @GetMapping("/discussion-detail") // 커뮤니티 게시글 상세 페이지 GET
    public String discussion_detail(@RequestParam Long id,@AuthenticationPrincipal Jwt jwt ,Model model){
        Post post = postService.findByPostId(id);
        List<Comment> comments = commentService.findAllByPostId(post);

        // 인증 된 사용자 검증
        Boolean Check = likeService.CheckLike(jwt, post); // 좋아요 체크

        Member member = memberService.findMemberByJwt(jwt).orElse(null);
        String nickname = member.getNickname();
        boolean isHost = post.getNickname().equals(nickname); // 게시물 작성자 체크
        boolean isLoot = member.IsLoot(); // 관리자 권한 체크

            model.addAttribute("host", isHost); //작성자 본인인지 확인
            model.addAttribute("nickname", nickname); //작성자 닉네임
            model.addAttribute("isLoot", isLoot);
            model.addAttribute("canDeletePost", isHost || isLoot);
            model.addAttribute("check", Check); // 사용자 좋아요/북마크 여부

        model.addAttribute("post",post); //게시글 정보
        model.addAttribute("comments",comments); // 댓글 정보

        return "MainService/community/discussion-detail";
    }

    @GetMapping("/createpost")
    public String createpost(){
        return "MainService/community/createpost";
    }

    @PostMapping("/createpost")
    public String SavePost(@AuthenticationPrincipal Jwt jwt, PostDto postDto){
        String nickname = memberService.findNicknameByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자만 게시글을 작성할 수 있습니다."));
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        postService.savePost(new Post(null,nickname,postDto.category(),postDto.industryTag(),postDto.title(),postDto.content(),simpleDateFormat.format(nowDate),0,0, Role.USER));

        return "redirect:/community";
    }

    @GetMapping("/editpost") // 수정 페이지 Get
    public String GetEditPost(Model model,@RequestParam Long id, @AuthenticationPrincipal Jwt jwt){
        Post post = postService.findByPostId(id);
        String nickname = memberService.findNicknameByJwt(jwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!post.getNickname().equals(nickname)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("postDto",new PostDto(post.getCategory(),post.getIndustryTag(),post.getTitle(),post.getContent()));
        model.addAttribute("postid",id);

        return "MainService/community/editpost";
    }

    @PostMapping("/editpost") // 게시글 수정
    public String EditPost(@ModelAttribute PostDto postDto, @RequestParam Long id, @AuthenticationPrincipal Jwt jwt){
        Post post = postService.findByPostId(id);
        String nickname = memberService.findNicknameByJwt(jwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!post.getNickname().equals(nickname)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        postService.editPost(post,postDto);
        return "redirect:/discussion-detail?id="+id;
    }

    //TODO : 쿼리단축 완료
    @PostMapping("/deletepost") // 게시글 삭제
    public String DeletePost(@RequestParam Long postid, @AuthenticationPrincipal Jwt jwt){
        Post post = postService.findByPostId(postid);
        Member member = memberService.findMemberByJwt(jwt).orElse(null);
        String nickname = member.getNickname();
        boolean isLoot = member.IsLoot();
        if (!isLoot && !post.getNickname().equals(nickname)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        postService.deletePost(postid);
        return "redirect:/community";
    }

    @ResponseBody
    @PostMapping("/likeAction") // 좋아요 등록
    public ResponseEntity<Boolean> LikeAction(@RequestParam Long id, @AuthenticationPrincipal Jwt jwt){
        LikeService.LikeActionResult result = likeService.PostLikeAction(jwt,id);
        return switch (result) {
            case SUCCESS -> new ResponseEntity<>(true, HttpStatus.OK);
            case UNAUTHORIZED -> new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
            case POST_NOT_FOUND -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            case DUPLICATE_LIKE -> new ResponseEntity<>(false, HttpStatus.CONFLICT);
            default -> new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        };
    }

    @ResponseBody
    @PostMapping("/unlikeAction") // 좋아요 취소
    public ResponseEntity<Boolean> UnLikeAction(@RequestParam Long id, @AuthenticationPrincipal Jwt jwt){
        LikeService.LikeActionResult result = likeService.PostUnLikeAction(jwt,id);
        return switch (result) {
            case SUCCESS -> new ResponseEntity<>(true, HttpStatus.OK);
            case UNAUTHORIZED -> new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
            case POST_NOT_FOUND -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            case LIKE_NOT_FOUND -> new ResponseEntity<>(false, HttpStatus.CONFLICT);
            default -> new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        };
    }

    //////////////////////////////////post

    @PostMapping("/createcomment") // 댓글 등록
    public String SaveComment(@AuthenticationPrincipal Jwt jwt, CommentDto commentDto){
        String nickname = memberService.findNicknameByJwt(jwt)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자만 댓글을 작성할 수 있습니다."));
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        commentService.saveComment(new Comment(null, postService.findByPostId(commentDto.postid()),nickname,commentDto.content(),simpleDateFormat.format(nowDate)));

        return "redirect:/discussion-detail?id="+commentDto.postid();
    }

    @PostMapping("/editcomment") // 댓글 수정
    public ResponseEntity<Integer> EditComment(@AuthenticationPrincipal Jwt jwt, Long id , String content){
        Comment comment = commentService.findById(id);
        String nickname = memberService.findNicknameByJwt(jwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!comment.getNickname().equals(nickname)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        commentService.editComment(id,content);
        return new ResponseEntity<>(0, HttpStatus.OK);
    }

    // TODO : 쿼리 단축 완료
    @PostMapping("/deletecomment") // 댓글 삭제
    public ResponseEntity<Integer> DeleteComment(@AuthenticationPrincipal Jwt jwt, Long id){
        Comment comment = commentService.findById(id);
        Member member = memberService.findMemberByJwt(jwt).orElse(null);
        String nickname = member.getNickname();
        boolean isLoot = member.IsLoot();
        if (!isLoot && !comment.getNickname().equals(nickname)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        commentService.DeleteCommentById(id);
        return new ResponseEntity<>(0, HttpStatus.OK);
    }



}
