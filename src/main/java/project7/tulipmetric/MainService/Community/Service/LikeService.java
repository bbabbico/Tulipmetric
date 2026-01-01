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
    public void PostLikeAction(Jwt jwt,Long postid) {
        log.info("{}", postid);
        Optional<Post> post = postRepository.findById(postid);

        if (post.isPresent()) {
            Post post1 = post.get();
            post1.setLikenum(post1.getLikenum()+1);
            postRepository.save(post1);
            likeRepository.save(new LikeEntity(null,jwt.getSubject(),post1));
            log.info("LikeEntity Action Successfully");
        } else{log.info("LikeEntity Action Failed : post is null");}
    }

    @Transactional
    public void PostUnLikeAction(Jwt jwt,Long postid) {
        log.info("{}", postid);
        Optional<Post> post = postRepository.findById(postid);

        if (post.isPresent()) {
            Post post1 = post.get();
            post1.setLikenum(post1.getLikenum()-1);
            postRepository.save(post1);
            likeRepository.deleteById(likeRepository.findByLoginidAndPostid(jwt.getSubject(),post1).getId());
            log.info("UnLike Action Successfully");
        } else{log.info("UnLike Action Failed : post is null");}
    }
}
