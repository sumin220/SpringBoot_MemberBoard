package board.myboard.domain.comment.controller;

import board.myboard.domain.comment.dto.CommentSaveDTO;
import board.myboard.domain.comment.dto.CommentUpdateDTO;
import board.myboard.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void commentSave(@PathVariable("postId") Long postId, CommentSaveDTO commentSaveDTO) {
        commentService.save(postId, commentSaveDTO);
    }


    @PostMapping("/comment/{postId}/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reCommentSave(@PathVariable("postId") Long postId,
                              @PathVariable("commentId") Long commentId,
                              CommentSaveDTO commentSaveDTO) {
        commentService.saveReComment(postId, commentId, commentSaveDTO);
    }


    @PutMapping("/comment/{commentId}")
    public void update(@PathVariable("commentId") Long commentId,
                       CommentUpdateDTO commentUpdateDTO) {
        commentService.update(commentId, commentUpdateDTO);
    }


    @DeleteMapping("/comment/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.remove(commentId);
    }
}
