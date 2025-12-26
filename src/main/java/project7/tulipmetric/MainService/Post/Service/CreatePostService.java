package project7.tulipmetric.MainService.Post.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Post.Post;
import project7.tulipmetric.domain.Post.PostRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePostService {
    public final PostRepository postRepository;

    public void SavePost(Post post){
        log.info("Post : {}", post);
        postRepository.save(post);
        log.info("Post Saved Successfully");
    }

    public Post FindByPostId(Long id){
        log.info("Post : {}", id);
        return postRepository.findById(id).get();
    }
}
