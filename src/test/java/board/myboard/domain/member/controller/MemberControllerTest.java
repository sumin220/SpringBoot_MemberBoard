package board.myboard.domain.member.controller;

import board.myboard.domain.member.dto.MemberSignUpDTO;
import board.myboard.domain.member.entity.Member;
import board.myboard.domain.member.exception.MemberException;
import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.domain.member.service.MemberService;
import board.myboard.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PasswordEncoder passwordEncoder;

    private static String SIGN_UP_URL = "/member/signUp";

    private String username = "username";
    private String password = "password1234!";
    private String name = "sumin";
    private String nickName = "suminzzang";
    private Integer age = 18;

    private void clear() {
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    private String getAccessToken() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        MvcResult result = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    @Test
    public void 회원가입_성공() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
        //when
        signUp(signUpData);
        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        assertThat(member.getName()).isEqualTo(name);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 회원가입_실패_필드가_없음() throws Exception {
        //given
        String noUsernameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(null, password, name, nickName, age));
        String noPasswordSignUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, null, name, nickName, age));
        String noNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, null, nickName, age));
        String noNickNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, null, age));
        String noAgeSignUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, null));
        //when
        signUp(noUsernameSignUpData);
        signUp(noPasswordSignUpData);
        signUp(noNameSignUpData);
        signUp(noNickNameSignUpData);
        signUp(noAgeSignUpData);

        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void 회원정보수정_성공() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
        signUp(signUpData);
        String accessToken = getAccessToken();
        Map<String, String> map = new HashMap<>();
        map.put("name", name + "변경");
        map.put("nickName", nickName + "변경");
        map.put("age", age.toString());
        String updateMemberData = objectMapper.writeValueAsString(map);
        //when
        mockMvc.perform(
                        put("/member")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        assertThat(member.getName()).isEqualTo(name + "변경");
        assertThat(member.getNickName()).isEqualTo(nickName + "변경");
        assertThat(member.getAge()).isEqualTo(age);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 회원정보수정_원하는필드만변경_성공() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
        signUp(signUpData);
        //when

        String accessToken = getAccessToken();
        Map<String, String> map = new HashMap<>();
        map.put("name", name + "변경");
        String updateMemberData = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                        put("/member")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        assertThat(member.getName()).isEqualTo(name + "변경");
        assertThat(member.getNickName()).isEqualTo(nickName);
        assertThat(member.getAge()).isEqualTo(age);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 비밀번호수정_성공() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, String> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("newPassword", password + "!@!@");

        String updatePassword = objectMapper.writeValueAsString(map);
        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());
        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password + "!@!@", member.getPassword())).isTrue();
    }

    @Test
    public void 회원탈퇴_성공() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();
        signUpData = accessToken;
        //when

        Map<String, String> map = new HashMap<>();
        map.put("checkPassword", password);

        String updatePassword = objectMapper.writeValueAsString(map);

        //then

        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());


        assertThrows(Exception.class,
                () -> memberRepository.findByUsername(username)
                        .orElseThrow(
                                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER)
                        ));
    }

//    @Test
//    public void 회원탈퇴_실패_비밀번호틀림() throws Exception {
//        //given
//        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
//        signUp(signUpData);
//        //when
//        String accessToken = getAccessToken();
//
//        Map<String, String> map = new HashMap<>();
//        map.put("checkPassword", password+11);
//
//        String updatePassword = objectMapper.writeValueAsString(map);
//
//        //then
//        mockMvc.perform(
//                delete("/member")
//                        .header(accessHeader, BEARER + accessToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(updatePassword))
//                .andExpect(status().isOk());
//
//        Member member = memberRepository.findByUsername(username).orElseThrow(
//                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
//
//        assertThat(member).isNotNull();
//    }

//    @Test
//    public void 내정보조회_성공() throws Exception {
//        //given
//        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, name, nickName, age));
//        signUp(signUpData);
//
//        String accessToken = getAccessToken();
//        //when
//        MvcResult result = mockMvc.perform(
//                get("/member")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .header(accessHeader, BEARER + accessToken))
//                .andExpect(status().isOk()).andReturn();
//
//        //then
//        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
//        Member member = memberRepository.findByUsername(username).orElseThrow(
//                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
//
//        assertThat(member.getAge()).isEqualTo(age);
//    }
}
