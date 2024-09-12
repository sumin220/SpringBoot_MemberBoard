package board.myboard.domain.post.exception;

import board.myboard.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {

    private final ErrorCode errorCode;
}
