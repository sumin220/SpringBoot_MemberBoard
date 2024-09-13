package board.myboard.domain.post.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService fileService;

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg", new FileInputStream("/Users/sonsumin/myboard_files/banner.jpg"));
    }

    @Test
    public void 파일저장_성공() throws Exception {
        //given
        String filePath = fileService.save(getMockUploadFile());
        //when
        File file = new File(filePath);
        assertThat(file.exists()).isTrue();
        //then
        file.delete();
    }

    @Test
    public void 파일삭제_성공() throws Exception {
        //given
        String filePath = fileService.save(getMockUploadFile());
        fileService.delete(filePath);
        //when

        File file = new File(filePath);
        assertThat(file.exists()).isFalse();

        //then

    }

}