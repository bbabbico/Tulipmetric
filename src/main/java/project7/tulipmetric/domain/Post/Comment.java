package project7.tulipmetric.domain.Post;

import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //댓글 식별 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post_id;

    @Column(length = 20 , nullable = false)
    private String nick_name; // 작성자 닉네임

    @Column(columnDefinition = "TEXT" ,nullable = false)
    private String content; // 글 내용

    @Column(length = 61 , nullable = false)
    private String date_minute; // 작성 년도/월/일/시간/분

    @Column(nullable = false)
    private int like_num; // 좋아요수

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", post_id=" + post_id +
                ", nick_name='" + nick_name + '\'' +
                ", content='" + content + '\'' +
                ", date_minute='" + date_minute + '\'' +
                ", like_num=" + like_num +
                '}';
    }
}
