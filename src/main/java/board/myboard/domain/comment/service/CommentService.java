package board.myboard.domain.comment.service;

import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.comment.exception.CommentException;

import java.util.List;

public interface CommentService {

    void save(Comment comment);

    Comment findById(Long id) throws CommentException;

    List<Comment> findAll();

    void remove(Long id) throws CommentException;
}
