package project7.tulipmetric.domain.Post;

import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //댓글 식별 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postid")
    private Post postid;

    @Column(length = 20 , nullable = false)
    private String nickname; // 작성자 닉네임

    @Column(columnDefinition = "TEXT" ,nullable = false)
    private String content; // 글 내용

    @Column(length = 61 , nullable = false)
    private String dateminute; // 작성 년도/월/일/시간/분

    @Column(nullable = false)
    private int likenum; // 좋아요수

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postid=" + postid +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", date_minute='" + dateminute + '\'' +
                ", likenum=" + likenum +
                '}';
    }
}
