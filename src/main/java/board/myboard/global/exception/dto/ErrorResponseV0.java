package board.myboard.global.exception.dto;

import lombok.Builder;

@Builder
public record ErrorResponseV0(

        String name,
        int errorCode,
        String message
){}
