package board.myboard.global.exception;

public abstract class BaseException extends RuntimeException {
    public abstract BaseExceptionType getExceptionType();
}
