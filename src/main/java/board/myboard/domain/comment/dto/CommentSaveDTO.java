package board.myboard.domain.comment.dto;

import board.myboard.domain.comment.entity.Comment;

public record CommentSaveDTO(
        String content
)
{
    public Comment toEntity() {
        return Comment.builder()
                .content(content)
                .build();
    }
}
