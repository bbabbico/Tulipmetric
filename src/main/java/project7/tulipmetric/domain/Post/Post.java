package project7.tulipmetric.domain.Post;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import project7.tulipmetric.domain.Member.Member;
import project7.tulipmetric.domain.Member.Role;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //글 식별 번호

    @Column(length = 20 , nullable = false)
    private String nick_name; // 작성자 닉네임

    @Column(length = 50 , nullable = false)
    private String title; // 글 제목

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content; // 글 내용

    @Column(length = 61 , nullable = false)
    private String date_minute; // 작성 년도/월/일/시간/분

    @Column(nullable = false)
    private int like_num; // 좋아요수

    @Column(nullable = false)
    private int commentnum; // 댓글수

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private Role role;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", nick_name='" + nick_name + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date_minute='" + date_minute + '\'' +
                ", like_num=" + like_num +
                ", commentnum=" + commentnum +
                ", role=" + role +
                '}';
    }
}
