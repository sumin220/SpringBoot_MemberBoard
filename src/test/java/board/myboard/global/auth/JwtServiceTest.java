package board.myboard.global.auth;

import board.myboard.domain.member.entity.Member;
import board.myboard.domain.member.entity.Role;
import board.myboard.domain.member.repository.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;



    //== 2 ==//
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";


    private String username = "username";

    @BeforeEach
    public void init() {
        Member member = Member.builder()
                .username(username)
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();
        memberRepository.save(member);
        clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    @Test
    public void createAccessToken_AccessToken_발급() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);

        DecodedJWT verify = getVerify(accessToken);
        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();
        //when

        //then
        assertThat(findUsername).isEqualTo(username);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);

    }

    @Test
    public void refreshToekn_발급() throws Exception {
        //given
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String username = verify.getClaim(USERNAME_CLAIM).asString();
        //when
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(username).isNull();

        //then

    }

    @Test
    public void updateRefreshToken_업데이트() throws Exception {
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(3000);
        //when
        String reIssueRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssueRefreshToken);
        clear();
        //then
        assertThat(memberRepository.findByRefreshToken(reIssueRefreshToken).get().getUsername()).isEqualTo(username);

    }


    @Test
    public void destroyRefreshToken_제거() throws Exception {
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        //when
        jwtService.destroyRefreshToken(username);
        clear();

        //then
        Member member = memberRepository.findByUsername(username).get();
        assertThat(member.getRefreshToken()).isNull();

    }

    @Test
    public void accessToken_헤더_설정() throws Exception {
        //given

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);
        //when
        jwtService.sendToken(mockHttpServletResponse, accessToken, refreshToken);

        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    public void sendToken_토큰_전송() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        //when
        jwtService.sendToken(mockHttpServletResponse, accessToken, refreshToken);
        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);

    }


    //== 토큰 추출 ==//

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendToken(mockHttpServletResponse,accessToken,refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;
    }

    @Test
    public void extractAccessToken_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);
        //when
        String extractAccessToken= jwtService.extractAccessToken(httpServletRequest);
        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
    }

    @Test
    public void extractUsername_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest);
        //when
        String extractUsername = jwtService.extractUsername(requestAccessToken);

        //then
        assertThat(extractUsername).isEqualTo(username);

    }
}