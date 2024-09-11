package board.myboard.domain.post.dto;

import board.myboard.domain.post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record PostSaveDTO(
        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @NotBlank(message = "내용을 입력해주세요")
        String content,

        Optional<MultipartFile> uploadFile
) {

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .build();
    }
}
