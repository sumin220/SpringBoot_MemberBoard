package board.myboard.global.filter;

import board.myboard.domain.member.entity.Member;
import board.myboard.domain.member.entity.Role;
import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.global.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class JwtFilterAuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "password";

    private static String LOGIN_URL = "/login";

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer";

    private ObjectMapper objectMapper = new ObjectMapper();

    private void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init() {
        memberRepository.save(Member.builder()
                .username(USERNAME)
                .password(delegatingPasswordEncoder.encode(PASSWORD))
                .name("Member1")
                .nickName("NickName")
                .role(Role.USER)
                .age(22)
                .build());
    }

    private Map getUsernamePasswordMap(String username, String password) {

        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private Map getAccessAndRefreshToken() throws Exception {

        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader,accessToken);
        tokenMap.put(refreshHeader,refreshToken);

        return tokenMap;
    }

}