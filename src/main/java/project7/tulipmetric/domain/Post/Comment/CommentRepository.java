package project7.tulipmetric.domain.Post.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import project7.tulipmetric.domain.Post.Post.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByPostid(Post postid);
    int countAllByPostid(Post postid);

    void deleteAllByPostid(Post postid);
}
