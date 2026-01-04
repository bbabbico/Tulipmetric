package project7.tulipmetric.domain.Post.Post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project7.tulipmetric.domain.Member.Role;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //글 식별 번호

    @Column(length = 20 , nullable = false)
    private String nickname; //작성자 식별 외래키

    @Setter
    @Column(length = 10 , nullable = false)
    private String category; // 글 카테고리

    @Setter
    @Column(length = 30 , nullable = false)
    private String industryTag; // 글 관련 산업군

    @Setter
    @Column(length = 50 , nullable = false)
    private String title; // 글 제목

    @Setter
    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content; // 글 내용

    @Column(length = 61 , nullable = false)
    private String dateminute; // 작성 년도/월/일/시간/분

    @Setter
    @Column(nullable = false)
    private int likenum; // 좋아요수

    @Setter
    @Column(nullable = false)
    private int commentnum; // 댓글수

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return likenum == post.likenum && commentnum == post.commentnum && Objects.equals(id, post.id) && Objects.equals(nickname, post.nickname) && Objects.equals(category, post.category) && Objects.equals(industryTag, post.industryTag) && Objects.equals(title, post.title) && Objects.equals(content, post.content) && Objects.equals(dateminute, post.dateminute) && role == post.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, category, industryTag, title, content, dateminute, likenum, commentnum, role);
    }

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
