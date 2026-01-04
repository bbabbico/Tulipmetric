package project7.tulipmetric.Mypage;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.MemberRepository;
import project7.tulipmetric.domain.Post.Comment.Comment;
import project7.tulipmetric.domain.Post.Comment.CommentRepository;
import project7.tulipmetric.domain.Post.Like.LikeEntity;
import project7.tulipmetric.domain.Post.Like.LikeRepository;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<Post> LoadPostsByNickname(Jwt jwt){ // 아이디로 Post 찾기
        String Nick_name =  memberRepository.findByLoginid(jwt.getSubject()).getNickname();
        return postRepository.findAllByNickname(Nick_name);
    }

    @Transactional
    public List<Post> LoadPostsByComment(Jwt jwt){ // Comment 로 Post 찾기
        List<Post> Posts = new ArrayList<>();

        Member member =  memberRepository.findByLoginid(jwt.getSubject());
        List<Comment> comments = commentRepository.findAllByNickname(member.getNickname());

        for (Comment comment : comments){
            if (Posts.contains(comment.getPostid())){
                Posts.add(comment.getPostid());
            }
        }
        return Posts;
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
