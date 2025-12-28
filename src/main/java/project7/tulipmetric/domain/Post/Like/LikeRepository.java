package project7.tulipmetric.domain.Post.Like;

import org.springframework.data.jpa.repository.JpaRepository;
import project7.tulipmetric.domain.Post.Post.Post;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity,Long> {
    List<LikeEntity> findAllByLoginid(String loginid);
    List<LikeEntity> findAllByPostid(Post postid);
    LikeEntity findByLoginidAndPostid(String loginid,Post postid);

    void deleteAllByPostid(Post postid);
}
