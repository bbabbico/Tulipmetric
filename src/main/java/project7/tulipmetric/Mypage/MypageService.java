package project7.tulipmetric.Mypage;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.MemberService;
import project7.tulipmetric.domain.Post.Comment.Comment;
import project7.tulipmetric.domain.Post.Comment.CommentRepository;
import project7.tulipmetric.domain.Post.Like.LikeEntity;
import project7.tulipmetric.domain.Post.Like.LikeRepository;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    @Transactional
    public List<Post> LoadPostsByNickname(Jwt jwt){ // 아이디로 Post 찾기
        String Nick_name =  memberService.findByLoginId(jwt.getSubject()).get().getNickname();
        return postRepository.findAllByNickname(Nick_name);
    }

    @Transactional
    public List<Post> LoadPostsByComment(Jwt jwt){ // Comment 로 Post 찾기

        Member member =  memberService.findByLoginId(jwt.getSubject()).get();
        List<Comment> comments = commentRepository.findAllByNickname(member.getNickname());

        Set<Long> postIds = new LinkedHashSet<>(); // 중복 제거 + 입력 순서 유지 , JPA HibernateProxy 에서 가져온 객체는 HibernateProxy 라는 대리객체 를 가져오므로 equals 에서 getClass() != o.getClass() 이거에 무조건 false 됨
        for (Comment c : comments) {
            Post p = c.getPostid();
            if (p != null && p.getId() != null) {
                postIds.add(p.getId());
            }
        }

        return postRepository.findAllById(postIds);
    }

    @Transactional
    public List<Post> LoadPostsByLike(Jwt jwt){ // Like 로 Post 찾기
        List<Post> Posts = new ArrayList<>();
        List<LikeEntity> Likes = likeRepository.findAllByLoginid(jwt.getSubject());

        for (LikeEntity likeEntity : Likes){
            Posts.add(likeEntity.getPostid());
        }
        return Posts;
    }
}
