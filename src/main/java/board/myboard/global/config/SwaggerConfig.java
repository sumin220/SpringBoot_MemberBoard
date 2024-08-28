package board.myboard.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("v1.0.0")               // 버전 기록
                        .title("P프 API")                // API 명세서 제목
                        .description("P프 API 명세서"));   // 상세

    }
}