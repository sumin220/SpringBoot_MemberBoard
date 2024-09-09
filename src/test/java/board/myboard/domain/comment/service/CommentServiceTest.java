package board.myboard.domain.comment.service;

import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.comment.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EntityManager em;

    private void clear() {
        em.flush();
        em.clear();
    }

    private Long saveComment() {
        Comment comment = Comment.builder()
                .content("댓글")
                .build();

        Long id = commentRepository.save(comment).getId();
        clear();
        return id;
    }

    private Long saveReComment(Long parentId) {
        Comment parent = commentRepository.findById(parentId).orElse(null);
        Comment comment = Comment.builder()
                .content("댓글")
                .parent(parent)
                .build();

        Long id = commentRepository.save(comment).getId();
        clear();

        return id;
    }

    /**
     * 댓글을 삭제하는 경우
     * 대댓글이 남아있는 경우
     * DB와 화면에서는 지워지지 않고, "삭제된 댓글입니다"로 표시
     */

    @Test
    public void 댓글삭제_대댓글이_남아있는_경우() throws Exception {
        //given
        Long commentId = saveComment();
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);

        assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(4);
        //when
        commentService.remove(commentId);
        clear();
        //then
        Comment findComment = commentService.findById(commentId);
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(4);
    }

}