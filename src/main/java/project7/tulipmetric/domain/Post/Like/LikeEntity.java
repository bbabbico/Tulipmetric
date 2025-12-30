package project7.tulipmetric.domain.Post.Like;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project7.tulipmetric.domain.Post.Post.Post;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100,nullable = false)
    private String loginid; //TODO : 나중에 닉네임 기반으로 통일해야함.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postid")
    private Post postid;
}
