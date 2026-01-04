package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Post.Like.LikeEntity;
import project7.tulipmetric.domain.Post.Like.LikeRepository;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    public final PostRepository postRepository;
    public final LikeRepository likeRepository;

    public enum LikeActionResult {
        SUCCESS,
        UNAUTHORIZED,
        POST_NOT_FOUND,
        DUPLICATE_LIKE,
        LIKE_NOT_FOUND
    }

    @Transactional
    public List<LikeEntity> findAllByPostid(Post post) {
        return likeRepository.findAllByPostid(post);
    }

    @Transactional
    public List<LikeEntity> findAllByLoginid(String loginid) {
        return likeRepository.findAllByLoginid(loginid);
    }

    @Transactional
    public Boolean CheckLike(Jwt jwt,Post post) { // 좋아요 이미 누른 사용자 인지 보내줌,
        if (jwt==null) {
            return false;
        }
        List<LikeEntity> postid = likeRepository.findAllByPostid(post);
        for  (LikeEntity likeEntity : postid) {
            if (likeEntity.getLoginid().equals(jwt.getSubject())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public LikeActionResult PostLikeAction(Jwt jwt,Long postid) {
        if (jwt == null) {
            log.warn("Unauthenticated like attempt for post {}", postid);
            return LikeActionResult.UNAUTHORIZED;
        }

        log.info("{}", postid);
        Optional<Post> post = postRepository.findById(postid);

        if (post.isEmpty()) {
            log.info("LikeEntity Action Failed : post is null");
            return LikeActionResult.POST_NOT_FOUND;
        }

        Post post1 = post.get();
        LikeEntity existingLike = likeRepository.findByLoginidAndPostid(jwt.getSubject(), post1);
        if (existingLike != null) {
            log.info("LikeEntity Action Skipped : already liked by {}", jwt.getSubject());
            return LikeActionResult.DUPLICATE_LIKE;
        }

        post1.setLikenum(post1.getLikenum()+1);
        postRepository.save(post1);
        likeRepository.save(new LikeEntity(null,jwt.getSubject(),post1));
        log.info("LikeEntity Action Successfully");
        return LikeActionResult.SUCCESS;
    }

    @Transactional
    public LikeActionResult PostUnLikeAction(Jwt jwt,Long postid) {
        if (jwt == null) {
            log.warn("Unauthenticated unlike attempt for post {}", postid);
            return LikeActionResult.UNAUTHORIZED;
        }

        log.info("{}", postid);
        Optional<Post> post = postRepository.findById(postid);

        if (post.isEmpty()) {
            log.info("UnLike Action Failed : post is null");
            return LikeActionResult.POST_NOT_FOUND;
        }

        Post post1 = post.get();
        LikeEntity existingLike = likeRepository.findByLoginidAndPostid(jwt.getSubject(),post1);
        if (existingLike == null) {
            log.info("UnLike Action Skipped : no like found for {} on post {}", jwt.getSubject(), postid);
            return LikeActionResult.LIKE_NOT_FOUND;
        }

        int currentLikes = post1.getLikenum();
        post1.setLikenum(Math.max(0, currentLikes - 1));
        postRepository.save(post1);
        likeRepository.deleteById(existingLike.getId());
        log.info("UnLike Action Successfully");
        return LikeActionResult.SUCCESS;
    }
}
