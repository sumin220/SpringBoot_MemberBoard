package board.myboard.domain.post.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record PostUpdateDTO(
        Optional<String> title,
        Optional<String> content,
        Optional<MultipartFile> uploadFile
) {
}
