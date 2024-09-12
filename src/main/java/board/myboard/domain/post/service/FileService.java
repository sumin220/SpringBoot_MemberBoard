package board.myboard.domain.post.service;

import board.myboard.domain.post.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String save(MultipartFile multipartFile) throws FileException;

    void delete(String filePath);
}
