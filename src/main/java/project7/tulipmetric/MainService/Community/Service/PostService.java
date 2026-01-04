package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project7.tulipmetric.domain.Post.Comment.CommentRepository;
import project7.tulipmetric.domain.Post.Like.LikeRepository;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostDto;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    public final PostRepository postRepository;
    public final CommentRepository commentRepository;
    public final LikeRepository likeRepository;

    @Transactional
    public void SavePost(Post post){
        postRepository.save(post);
        log.info("Post Saved Successfully");
    }

    @Transactional
    public void EditPost(Post post, PostDto postDto){
        post.setCategory(postDto.category());
        post.setIndustryTag(postDto.industryTag());
        post.setTitle(postDto.title());
        post.setContent(postDto.content());

        postRepository.save(post);
    }

    @Transactional
    public void DeletePost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));

        likeRepository.deleteAllByPostid(post);
        commentRepository.deleteAllByPostid(post);
        postRepository.deleteById(id);
    }

    @Transactional
    public String NickNameFindByPostid(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));
        return post.getNickname();
    }

    @Transactional
    public Post FindByPostId(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));
    }

    @Transactional
    public List<Post> FindAllByNickname(String nickname){
        return postRepository.findAllByNickname(nickname);
    }

    @Transactional
    public List<Post> FindAll(){return postRepository.findAll();}
}
