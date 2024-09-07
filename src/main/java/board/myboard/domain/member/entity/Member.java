package board.myboard.domain.member.entity;

import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.post.entity.Post;
import board.myboard.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
@Table(name = "MEMBER")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; //PK

    @Column(nullable = false, length = 30, unique = true)
    private String username; //아이디

    private String password; //비밀번호

    @Column(nullable = false, length = 30)
    private String name; //이름(실명)

    @Column(nullable = false, length = 30)
    private String nickName; //별명

    @Column(nullable = false, length = 30)
    private Integer age; //나이

    @Enumerated(EnumType.STRING)
    private Role role; //권한 -> USER, ADMOIN

    @Column(length = 1000)
    private String refreshToken; //리프레시토큰


    //== 회원탈퇴 -> 작성한 게시물, 댓글 모두 삭제 ==//
    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();


    //== 연관관계 메서드 ==//
    public void addPost(Post post) {
        //post의 writer 설정은 post에서 함
        postList.add(post);
    }

    public void addComment(Comment comment) {
        //comment의 writer 설정은 comment에서 함
        commentList.add(comment);
    }

    //== 정보 수정 ==//
    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateAge(Integer age) {
        this.age = age;
    }

    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    //== 토큰 관련 ==//
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }
}
