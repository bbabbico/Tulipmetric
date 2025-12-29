package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        likeRepository.deleteAllByPostid(postRepository.findById(id).get());
        commentRepository.deleteAllByPostid(postRepository.findById(id).get());
        postRepository.deleteById(id);
    }

    @Transactional
    public String NickNameFindByPostid(Long id){
        return postRepository.findById(id).get().getNickname();}

    @Transactional
    public Post FindByPostId(Long id){
        return postRepository.findById(id).get();
    }

    @Transactional
    public List<Post> FindAllByNickname(String nickname){
        return postRepository.findAllByNickname(nickname);
    }

    @Transactional
    public List<Post> FindAll(){return postRepository.findAll();}
}
