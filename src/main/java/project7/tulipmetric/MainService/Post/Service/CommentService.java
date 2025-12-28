package project7.tulipmetric.MainService.Post.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public List<Comment> FindAllByPostid(Post post){
        return commentRepository.findAllByPostid(post);
    }
}
