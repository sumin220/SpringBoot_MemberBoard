package board.myboard.domain.post.service;

import board.myboard.domain.member.dto.MemberSignUpDTO;
import board.myboard.domain.member.entity.Role;
import board.myboard.domain.member.service.MemberService;
import board.myboard.domain.post.dto.PostSaveDTO;
import board.myboard.domain.post.dto.PostUpdateDTO;
import board.myboard.domain.post.entity.Post;
import board.myboard.domain.post.exception.PostException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password123@@@";

    private void clear() {
        em.flush();
        em.clear();
    }

    private void deleteFile(String filePath) {
        File files = new File(filePath);
        files.delete();
    }

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg", new FileInputStream(("/Users/sonsumin/myboard_files/banner.jpg")));
    }

    @BeforeEach
    public void signUpAndSetAuthentication() throws Exception {
        memberService.signup(new MemberSignUpDTO(USERNAME, PASSWORD, "name", "nickName", 22));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null
                )
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 포스트_저장_성공_업로드_파일_없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        //when
        postService.save(postSaveDTO);
        clear();
        //then
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNull();

    }

    @Test
    public void 포스트_저장_성공_업로드_파일_있음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.ofNullable(getMockUploadFile()));

        //when
        postService.save(postSaveDTO);
        clear();
        //then
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNotNull();

        deleteFile(post.getFilePath());
    }

    @Test
    public void 포스트_저장_실패_제목이나_내용이_없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";

        PostSaveDTO postSaveDTO1 = new PostSaveDTO(null, content, Optional.empty());
        PostSaveDTO postSaveDTO2 = new PostSaveDTO(title, null, Optional.empty());
        //when

        //then

        assertThrows(Exception.class, () -> postService.save(postSaveDTO1));
        assertThrows(Exception.class, () -> postService.save(postSaveDTO2));
    }

    @Test
    public void 포스트_업데이트_성공_업로드파일_없음TOd없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        postService.save(postSaveDTO);
        clear();
        //when
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        PostUpdateDTO postUpdateDTO = new PostUpdateDTO(Optional.ofNullable("바꾼제목"), Optional.ofNullable("바꾼내용"), Optional.empty());
        postService.update(findPost.getId(), postUpdateDTO);
        clear();
        //then
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo("바꾼내용");

    }

    @Test
    public void 포스트_업데이트_성공_업로드파일_있음TO없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.ofNullable(getMockUploadFile()));
        postService.save(postSaveDTO);

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        assertThat(findPost.getFilePath()).isNotNull();
        clear();
        //when

        PostUpdateDTO postUpdateDTO = new PostUpdateDTO(Optional.ofNullable("바꾼제목"), Optional.ofNullable("바꾼내용"), Optional.empty());
        postService.update(findPost.getId(), postUpdateDTO);
        //then

        findPost = em.find(Post.class, findPost.getId());
        assertThat(findPost.getContent()).isEqualTo("바꾼내용");
        assertThat(findPost.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(findPost.getFilePath()).isNull();

    }

    @Test
    public void 포스트_업데이트_성공_업로드파일_있음TO있음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        postService.save(postSaveDTO);

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        String filePath = post.getFilePath();
        clear();

        //when
        PostUpdateDTO postUpdateDTO = new PostUpdateDTO(Optional.ofNullable("바꾼제목"),Optional.ofNullable("바꾼내용"), Optional.ofNullable(getMockUploadFile()));
        postService.update(findPost.getId(),postUpdateDTO);
        clear();

        //then
        post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo("바꾼내용");
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNotEqualTo(filePath);
        deleteFile(post.getFilePath());
        // 올린 파일 삭제
    }

    private void setAnotherAuthentication() throws Exception {
        memberService.signup(new MemberSignUpDTO(USERNAME + "123", PASSWORD, "name", "nickName", 22));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME + "123")
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null
                )
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 포스트_업데이트_실패_권한이없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        postService.save(postSaveDTO);
        clear();
        //when
        setAnotherAuthentication();
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        PostUpdateDTO postUpdateDTO = new PostUpdateDTO(Optional.ofNullable("바꾼제목"), Optional.ofNullable("바꾼내용"), Optional.empty());

        assertThrows(PostException.class, () -> postService.update(findPost.getId(), postUpdateDTO));
    }

    @Test
    public void 포스트삭제_성공() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        postService.save(postSaveDTO);
        clear();
        //when
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        postService.delete(findPost.getId());

        //then
        List<Post> findPosts = em.createQuery("select p from Post p", Post.class).getResultList();
        assertThat(findPosts.size()).isEqualTo(0);
    }

    @Test
    public void 포스트삭제_실패() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        postService.save(postSaveDTO);
        clear();
        //when
        setAnotherAuthentication();
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        assertThrows(PostException.class, () -> postService.delete(findPost.getId()));
        //then

    }
}