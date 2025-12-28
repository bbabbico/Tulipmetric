package project7.tulipmetric.domain.Post.Comment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project7.tulipmetric.domain.Post.Post.Post;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //댓글 식별 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postid")
    private Post postid;

    @Column(length = 20 , nullable = false)
    private String nickname; //작성자 식별 외래키

    @Setter
    @Column(columnDefinition = "TEXT" ,nullable = false)
    private String content; // 글 내용

    @Column(length = 61 , nullable = false)
    private String dateminute; // 작성 년도/월/일/시간/분

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postid=" + postid +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", dateminute='" + dateminute + '\'' +
                '}';
    }
}
