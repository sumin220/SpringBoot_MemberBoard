package board.myboard.domain.comment.exception;

import board.myboard.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentException extends RuntimeException {

    private final ErrorCode errorCode;
}
