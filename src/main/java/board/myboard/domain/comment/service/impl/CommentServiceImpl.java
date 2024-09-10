package board.myboard.domain.comment.service.impl;

import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.comment.exception.CommentException;
import board.myboard.domain.comment.repository.CommentRepository;
import board.myboard.domain.comment.service.CommentService;
import board.myboard.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public void save(Comment comment) {

        commentRepository.save(comment);
    }

    @Override
    public Comment findById(Long id) throws CommentException {
        return commentRepository.findById(id).orElseThrow(
                () -> new CommentException(ErrorCode.NOT_EXIST_COMMENT));
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public void remove(Long id) throws CommentException {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentException(ErrorCode.NOT_EXIST_COMMENT));

        comment.remove();
        List<Comment> removableCommentList = comment.findRemovableList();
        removableCommentList.forEach(removableComment -> commentRepository.delete(removableComment));
    }
}
