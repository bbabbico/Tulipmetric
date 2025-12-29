package project7.tulipmetric.domain.Post.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByNickname(String nickname);
}
