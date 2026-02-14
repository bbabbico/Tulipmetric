package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project7.tulipmetric.domain.Member.MemberRepository;
import project7.tulipmetric.domain.Post.Comment.Comment;
import project7.tulipmetric.domain.Post.Comment.CommentRepository;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    public final CommentRepository commentRepository;
    public final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveComment(Comment comment) {
        log.info("{}", comment);
        Post post = postRepository.findById(comment.getPostid().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));

        commentRepository.save(comment);
        post.setCommentnum(commentRepository.countAllByPostid(post));
        postRepository.save(post);
        log.info("comment Saved Successfully");
    }

    @Transactional
    public void editComment(Long id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Transactional
    public void DeleteCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
        Post post = comment.getPostid();
        post.setCommentnum(post.getCommentnum() - 1);
        commentRepository.deleteById(id);
    }

    public List<Comment> findAllByNickname(String nickname) {
        return commentRepository.findAllByNickname(nickname);
    }

    public List<Comment> findAllByPostId(Post post) {
        return commentRepository.findAllByPostid(post);
    }

    public int countByJwt(Jwt jwt) {
        return commentRepository.findAllByNickname(memberRepository.findByLoginid(jwt.getSubject()).getNickname())
                .size();
    }

    @Transactional
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
    }
}
