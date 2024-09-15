package board.myboard.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("v1.0.0")               // 버전 기록
                        .title("내 게시판 API")                // API 명세서 제목
                        .description("API 명세서"));   // 상세

    }
}