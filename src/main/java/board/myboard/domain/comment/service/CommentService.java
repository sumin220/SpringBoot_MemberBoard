package board.myboard.domain.comment.service;

import board.myboard.domain.comment.dto.CommentSaveDTO;
import board.myboard.domain.comment.dto.CommentUpdateDTO;
import board.myboard.domain.comment.exception.CommentException;

public interface CommentService {

    void save(Long postId, CommentSaveDTO commentSaveDTO);

    void saveReComment(Long postId, Long parentId, CommentSaveDTO commentSaveDTO);

    void update(Long id, CommentUpdateDTO commentUpdateDTO);

    void remove(Long id) throws CommentException;
}
