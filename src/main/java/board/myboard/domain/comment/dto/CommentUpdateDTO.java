package board.myboard.domain.comment.dto;

import java.util.Optional;

public record CommentUpdateDTO(
        Optional<String> content
) {
}
