package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.MemberRepository;
import project7.tulipmetric.domain.Post.Comment.Comment;
import project7.tulipmetric.domain.Post.Comment.CommentRepository;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    public final CommentRepository commentRepository;
    public final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void SaveComment(Comment comment){
        log.info("{}", comment);
        Optional<Post> post = postRepository.findById(comment.getPostid().getId());

        if (post.isPresent()) {
            commentRepository.save(comment);
            post.get().setCommentnum(commentRepository.countAllByPostid(comment.getPostid()));
            postRepository.save(post.get());
            log.info("comment Saved Successfully");
        } else{log.info("comment Saved Failed : post is null");}
    }

    @Transactional
    public void EditComment(Long id, String content){
        Comment comment = commentRepository.findById(id).get();
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Transactional
    public void DeleteCommentById(Long id){
        Post post = commentRepository.findById(id).get().getPostid();
        post.setCommentnum(post.getCommentnum()-1);
        commentRepository.deleteById(id);
    }

    @Transactional
    public List<Comment> FindAllByNickname(String nickname){
        return commentRepository.findAllByNickname(nickname);
    }


    public List<Comment> FindAllByPostid(Post post){
        return commentRepository.findAllByPostid(post);
    }

    public int CommentCountFindJwt(Jwt jwt){
       return commentRepository.findAllByNickname(memberRepository.findByLoginid(jwt.getSubject()).getNickname()).size();
    }

    @Transactional
    public Comment FindById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }
}
