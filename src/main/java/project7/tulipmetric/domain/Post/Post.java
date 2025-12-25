package project7.tulipmetric.domain.Post;

import jakarta.persistence.*;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.Role;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //글 식별 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userid")
    private Member userid; //작성자 식별 외래키

    @Column(length = 50 , nullable = false)
    private String title; // 글 제목

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content; // 글 내용

    @Column(length = 61 , nullable = false)
    private String dateminute; // 작성 년도/월/일/시간/분

    @Column(nullable = false)
    private int likenum; // 좋아요수

    @Column(nullable = false)
    private int commentnum; // 댓글수

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private Role role;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", dateminute='" + dateminute + '\'' +
                ", likenum=" + likenum +
                ", commentnum=" + commentnum +
                ", role=" + role +
                '}';
    }
}
