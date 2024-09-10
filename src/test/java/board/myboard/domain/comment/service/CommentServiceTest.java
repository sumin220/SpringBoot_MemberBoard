package board.myboard.domain.comment.service;

import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.comment.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    /**
     * 댓글을 삭제하는 경우
     * 대댓글이 존재하나 모두 삭제된 경우
     * 댓글과, 달려있는 대댓글 모두 DB에서 일괄 삭제, 화면 상에도 표시되지 않음
     */

    @Test
    public void 댓글삭제_대댓글이_존재하나_모두_삭제된_대댓글() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommentId1 = saveReComment(commentId);
        Long reCommentId2 = saveReComment(commentId);
        Long reCommentId3 = saveReComment(commentId);
        Long reCommentId4 = saveReComment(commentId);
        //when
        assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(4);
        clear();

        commentService.remove(reCommentId1);
        clear();

        commentService.remove(reCommentId2);
        clear();

        commentService.remove(reCommentId3);
        clear();

        commentService.remove(reCommentId4);
        clear();

        //then
        assertThat(commentService.findById(reCommentId1).isRemoved()).isTrue();
        assertThat(commentService.findById(reCommentId2).isRemoved()).isTrue();
        assertThat(commentService.findById(reCommentId3).isRemoved()).isTrue();
        assertThat(commentService.findById(reCommentId4).isRemoved()).isTrue();
        clear();

        commentService.remove(commentId);
        clear();

        LongStream.rangeClosed(commentId, reCommentId4).forEach(id ->
                assertThat(assertThrows(Exception.class,
                        () -> commentService.findById(id)).getMessage()).isEqualTo("댓글이 없습니다."));


    }


    /**
     * 대댓글을 삭제하는 경우
     * 부모 댓글이 삭제되지 않는 경우
     * 내용만 삭제, DB에서는 삭제x
     */

    @Test
    public void 대댓글삭제_부모댓글이_남아있는_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommentId = saveReComment(commentId);
        //when
        commentService.remove(reCommentId);
        clear();
        //then
        assertThat(commentService.findById(commentId)).isNotNull();
        assertThat(commentService.findById(reCommentId)).isNotNull();
        assertThat(commentService.findById(commentId).isRemoved()).isFalse();
        assertThat(commentService.findById(reCommentId).isRemoved()).isTrue();
    }

    /**
     * 대댓글을 삭제하는 경우
     * 부모 댓글이 삭제되어있고, 대댓글도 모두 삭제된 경우
     * 부모를 포함한 모든 대댓글을 DB에서 일괄 삭제, 화면상에서도 지움
     */
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_모든_대댓글이_삭제된_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommentId1 = saveReComment(commentId);
        Long reCommentId2 = saveReComment(commentId);
        Long reCommentId3 = saveReComment(commentId);
        //when
        commentService.remove(commentId);
        clear();

        commentService.remove(reCommentId2);
        clear();

        commentService.remove(reCommentId3);
        clear();

        assertThat(commentService.findById(commentId)).isNotNull();
        assertThat(commentService.findById(commentId).getChildList()).size().isEqualTo(3);

        commentService.remove(reCommentId1);
        //then

        LongStream.rangeClosed(commentId, reCommentId3).forEach(id ->
                assertThat(assertThrows(Exception.class,
                        () -> commentService.findById(id)).getMessage()).isEqualTo("댓글이 없습니다."));

    }

    /**
     * 대댓글을 삭제하는 경우
     * 부모 댓글이 삭제되어있고, 다른 대댓글이 아직 삭제되지 않고 남아있는 경우
     * 해당 대댓글만 삭제, 그러나 DB에서 삭제되지는 않고, 화면상에는 "삭제된 댓글입니다" 라고 표시
     */
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_다른_대댓글이_남아있는_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommentId1 = saveReComment(commentId);
        Long reCommentId2 = saveReComment(commentId);
        Long reCommentId3 = saveReComment(commentId);

        commentService.remove(reCommentId3);
        commentService.remove(commentId);
        clear();

        assertThat(commentService.findById(commentId)).isNotNull();
        assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(3);
        //when

        commentService.remove(reCommentId2);
        assertThat(commentService.findById(commentId)).isNotNull();
        //then
        assertThat(commentService.findById(reCommentId2)).isNotNull();
        assertThat(commentService.findById(reCommentId2).isRemoved()).isTrue();
        assertThat(commentService.findById(reCommentId1).getId()).isNotNull();
        assertThat(commentService.findById(reCommentId3).getId()).isNotNull();
        assertThat(commentService.findById(commentId).getId()).isNotNull();
    }
}