package project7.tulipmetric.MainService.Post.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Post.Comment;
import project7.tulipmetric.domain.Post.CommentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCommentService {
    public final CommentRepository commentRepository;

    public void SavePost(Comment comment){
        log.info("Post : {}", comment);
        commentRepository.save(comment);
        log.info("comment Saved Successfully");

    }
}
