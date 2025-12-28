package project7.tulipmetric.MainService.Post.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Post.Post.Post;
import project7.tulipmetric.domain.Post.Post.PostDto;
import project7.tulipmetric.domain.Post.Post.PostRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    public final PostRepository postRepository;

    public void SavePost(Post post){
        postRepository.save(post);
        log.info("Post Saved Successfully");
    }

    public void EditPost(Post post, PostDto postDto){
        post.setCategory(postDto.category());
        post.setIndustryTag(postDto.industryTag());
        post.setTitle(postDto.title());
        post.setContent(postDto.content());

        postRepository.save(post);
    }

    public void DeletePost(Long id){
        postRepository.deleteById(id);
    }

    public String NickNameFindByPostid(Long id){
        return postRepository.findById(id).get().getNickname();}

    public Post FindByPostId(Long id){
        return postRepository.findById(id).get();
    }
    public List<Post> FindAll(){return postRepository.findAll();}
}
