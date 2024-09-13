package board.myboard.domain.comment.service.impl;

import board.myboard.domain.comment.dto.CommentSaveDTO;
import board.myboard.domain.comment.dto.CommentUpdateDTO;
import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.comment.exception.CommentException;
import board.myboard.domain.comment.repository.CommentRepository;
import board.myboard.domain.comment.service.CommentService;
import board.myboard.domain.member.exception.MemberException;
import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.domain.post.exception.PostException;
import board.myboard.domain.post.repository.PostRepository;
import board.myboard.global.exception.ErrorCode;
import board.myboard.global.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public void save(Long postId, CommentSaveDTO commentSaveDTO) {

        Comment comment = commentSaveDTO.toEntity();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(
                        () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER)
                ));

        comment.confirmPost(postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostException(ErrorCode.NOT_FOUND_BOARD)
                ));

        commentRepository.save(comment);
    }


    @Override
    public void saveReComment(Long postId, Long parentId, CommentSaveDTO commentSaveDTO) {
        Comment comment = commentSaveDTO.toEntity();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER)));

        comment.confirmPost(postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_BOARD)));

        comment.confirmParent(commentRepository.findById(parentId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_COMMENT)));

        commentRepository.save(comment);
    }

    @Override
    public void update(Long id, CommentUpdateDTO commentUpdateDTO) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(ErrorCode.NOT_EXIST_COMMENT));

        if (!comment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new CommentException(ErrorCode.NOT_AUTHORITY_UPDATE_COMMENT);
        }
        commentUpdateDTO.content().ifPresent(comment::updateContent);
    }

    @Override
    public void remove(Long id) throws CommentException {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentException(ErrorCode.NOT_EXIST_COMMENT));

        if (!comment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new CommentException(ErrorCode.NOT_AUTHORITY_UPDATE_COMMENT);
        }

        comment.remove();
        List<Comment> removableCommentList = comment.findRemovableList();
        commentRepository.deleteAll(removableCommentList);
    }
}
