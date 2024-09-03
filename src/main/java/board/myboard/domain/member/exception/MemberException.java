package board.myboard.domain.member.exception;

import board.myboard.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberException {

    private final ErrorCode errorCode;
}