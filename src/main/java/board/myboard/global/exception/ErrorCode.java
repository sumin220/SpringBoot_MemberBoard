package board.myboard.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* 사용자 */
    ALREADY_EXIST_MEMBER(00, "이미 존재하는 사용자입니다."),
    NOT_FOUND_MEMBER(00, "사용자를 찾을 수 없습니다."),
    MISMATCH_PASSWORD(00, "비밀번호가 일치하지 않습니다."),

    /* 게시판 & 공지 */
    NOT_FOUND_TITLE(00, "제목을 입력하세요."),
    NOT_FOUND_CONTENT(00, "내용을 입력하세요."),
    TITLE_MAX_NUMBER(00, "최대 글자 수를 초과했습니다."),
    NOT_FOUND_BOARD(00, "게시물을 찾을 수 없습니다."),
    ALREADY_DELETED(00, "이미 삭제된 게시물입니다."),

    /* 파일 */
    FILE_OVERSIZE(00, "파일 용량을 초과했습니다."),
    NOT_FOUND_FILE(00, "파일을 찾을 수 없습니다."),

    /* 캘린더 */
    NOT_FOUND_SCHEDULE(00, "일정을 찾을 수 없습니다.") ,
    MAX_PARTICIPANTS(00, "최대 인원을 초과했습니다.") ,
    SCHEDULE_OVERLAP(00, "이미 참여중인 일정입니다.") ,
    NOT_FOUND_SCHEDULE_TITLE(00, "제목을 입력하세요.") ,
    NOT_CHOSEN_DATE(00, "날짜를 선택하세요.") ,
    NOT_CHOSEN_CATEGORY(00, "카테고리를 선택하세요.") ,
    ACCESS_DENIED(00, "권한이 없습니다.") ,


    /* 공용 */
    NOT_FOUND_ACCESS_TOKEN(00, "토큰을 찾을 수 없습니다."),
    EXPIRED_ACCESS_TOKEN(403, "토큰의 유효시간이 만료되었습니다.");

    private final int errorCode;
    private final String message;

}
