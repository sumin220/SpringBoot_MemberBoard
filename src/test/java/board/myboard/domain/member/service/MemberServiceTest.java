package board.myboard.domain.member.service;

import board.myboard.domain.member.dto.MemberInfoDTO;
import board.myboard.domain.member.dto.MemberSignUpDTO;
import board.myboard.domain.member.dto.MemberUpdateDTO;
import board.myboard.domain.member.entity.Member;
import board.myboard.domain.member.entity.Role;
import board.myboard.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    String PASSWORD = "password";

    private void clear() {
        em.flush();
        em.clear();
    }

    private MemberSignUpDTO makeMemberSignUpDTO() {
        return new MemberSignUpDTO("username", PASSWORD, "name", "nickName", 22);
    }

    /**
     * 회원가입 후 자동으로 로그인 상태
     */
    private MemberSignUpDTO setMember() throws Exception {

        MemberSignUpDTO memberSignUpDTO = makeMemberSignUpDTO();
        memberService.signup(memberSignUpDTO);
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(memberSignUpDTO.username())
                .password(memberSignUpDTO.password())
                .roles(Role.USER.name())
                .build(),
                null, null));

        SecurityContextHolder.setContext(emptyContext);
        return memberSignUpDTO;
    }

    @AfterEach
    public void removeMember() {
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    /**회원가입
     *  회원가입 시 아이디, 비밀번호, 이름, 별명, 나이를 입력하지 않으면 오류
     *  이미 존재하는 아이디가 있으면 오류
     *  회원가입 후 회원의 role은 USER
     */

    @Test
    public void 회원가입_성공() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = makeMemberSignUpDTO();
        //when
        memberService.signup(memberSignUpDTO);
        clear();
        //then
        Member member =  memberRepository.findByUsername(memberSignUpDTO.username()).orElseThrow(
                () -> new Exception("회원이 존재하지 않습니다"));

        assertThat(member.getId()).isNotNull();
        assertThat(member.getUsername()).isEqualTo(memberSignUpDTO.username());
        assertThat(member.getName()).isEqualTo(memberSignUpDTO.name());
        assertThat(member.getNickName()).isEqualTo(memberSignUpDTO.nickName());
        assertThat(member.getAge()).isEqualTo(memberSignUpDTO.age());
        assertThat(member.getRole()).isEqualTo(Role.USER);
    }

    @Test
    public void 아이디중복_회원가입_실패() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = makeMemberSignUpDTO();
        memberService.signup(memberSignUpDTO);
        clear();
        //when

        //then
        assertThat(assertThrows(Exception.class, () -> memberService.signup(memberSignUpDTO))
                .getMessage()).isEqualTo("이미 존재하는 아이디입니다");

    }
    
    @Test
    public void 입력하지않은_필드_회원가입_실패() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO1 = new MemberSignUpDTO(null, passwordEncoder.encode(PASSWORD), "name", "nickName", 22);
        MemberSignUpDTO memberSignUpDTO2 = new MemberSignUpDTO("username", null, "name", "nickName", 22);
        MemberSignUpDTO memberSignUpDTO3 = new MemberSignUpDTO("username", passwordEncoder.encode(PASSWORD), null, "nickName", 22);
        MemberSignUpDTO memberSignUpDTO4 = new MemberSignUpDTO("username", passwordEncoder.encode(PASSWORD), "name", null, 22);
        MemberSignUpDTO memberSignUpDTO5 = new MemberSignUpDTO("username", passwordEncoder.encode(PASSWORD), "name", "nickName", null);
        //when
        
        //then

        assertThrows(Exception.class, () -> memberService.signup(memberSignUpDTO1));
        assertThrows(Exception.class, () -> memberService.signup(memberSignUpDTO2));
        assertThrows(Exception.class, () -> memberService.signup(memberSignUpDTO3));
        assertThrows(Exception.class, () -> memberService.signup(memberSignUpDTO4));
        assertThrows(Exception.class, () -> memberService.signup(memberSignUpDTO5));
    
    }

    /**
     * 회원정보 수정
     * 회원가입을 하지 않은 사람이 정보 수정 시 오류 -> 시큐리티 필터가 알아서 막아줌
     * 아이디는 변경 불가능
     * 비밀번호 변경 시 현재 비밀번호를 입력받아서 일치한 경우에만 바꿀 수 있음
     * 비밀번호 변경 시에는 오직 비밀번호만 바꿀 수 있음
     *
     * 비밀번호가 아닌 이름, 별명, 나이 변경 시에는 3개를 한 번에 바꿀 수도 있고, 한 두 개만 선택해서 바꿀 수도 있음
     * 아무것도 바뀌는 게 없는데 변경요청을 보내면 오류
     */

    @Test
    public void 회원수정_비밀번호수정_성공() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        String newPassword = "12345!@#$%";
        memberService.updatePassword(PASSWORD, newPassword);
        clear();
        //then
        Member findMember = memberRepository.findByUsername(memberSignUpDTO.username()).orElseThrow(
                () -> new Exception());

        assertThat(findMember.matchPassword(passwordEncoder, newPassword)).isTrue();
    }

    @Test
    public void 회원수정_이름만수정() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        String updateName = "오류으악";
        memberService.update(new MemberUpdateDTO(Optional.of(updateName), Optional.empty(), Optional.empty()));
        clear();
        //then
        memberRepository.findByUsername(memberSignUpDTO.username()).ifPresent((member -> {
            assertThat(member.getName()).isEqualTo(updateName);
            assertThat(member.getAge()).isEqualTo(memberSignUpDTO.age());
            assertThat(member.getNickName()).isEqualTo(memberSignUpDTO.nickName());
        }));
    }

    @Test
    public void 회원수정_별명만수정() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        String updateNickname = "오류개발자";
        memberService.update(new MemberUpdateDTO(Optional.empty(), Optional.of(updateNickname), Optional.empty()));
        clear();
        //then
        memberRepository.findByUsername(memberSignUpDTO.username()).ifPresent((member -> {
            assertThat(member.getName()).isEqualTo(memberSignUpDTO.name());
            assertThat(member.getAge()).isEqualTo(memberSignUpDTO.age());
            assertThat(member.getNickName()).isEqualTo(updateNickname);
        }));
    }

    @Test
    public void 회원수정_나이만수정() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        Integer updateAge = 100;
        memberService.update(new MemberUpdateDTO(Optional.empty(), Optional.empty(), Optional.of(updateAge)));
        clear();
        //then
        memberRepository.findByUsername(memberSignUpDTO.username()).ifPresent(member -> {
            assertThat(member.getName()).isEqualTo(memberSignUpDTO.name());
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getNickName()).isEqualTo(memberSignUpDTO.nickName());
        });
    }

    @Test
    public void 회원수정_이름_별명_수정() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        String updateName = "호호호";
        String updateNickName = "버그개발자";
        memberService.update(new MemberUpdateDTO(Optional.of(updateName), Optional.of(updateNickName), Optional.empty()));
        clear();
        //then
        memberRepository.findByUsername(memberSignUpDTO.username()).ifPresent(member -> {
            assertThat(member.getName()).isEqualTo(updateName);
            assertThat(member.getNickName()).isEqualTo(updateNickName);
            assertThat(member.getAge()).isEqualTo(memberSignUpDTO.age());
        });
    }

    @Test
    public void 회원탈퇴() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        memberService.withdraw(PASSWORD);

        //then
        assertThat(assertThrows(Exception.class, () -> memberRepository.findByUsername(memberSignUpDTO.username())
                .orElseThrow(() -> new Exception("회원이 존재하지 않습니다.")))
                .getMessage()).isEqualTo("회원이 존재하지 않습니다.");
    }

    @Test
    public void 회원탈퇴_실패_비밀번호_불일치() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when

        //then
        assertThat(assertThrows(Exception.class, () -> memberService.withdraw(PASSWORD + "1"))
                .getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    @Test
    public void 회원정보조회() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        Member member = memberRepository.findByUsername(memberSignUpDTO.username())
                .orElseThrow(() -> new Exception());
        clear();
        //when
        MemberInfoDTO info = memberService.getInfo(member.getId());

        //then
        assertThat(info.getUsername()).isEqualTo(memberSignUpDTO.username());
        assertThat(info.getNickName()).isEqualTo(memberSignUpDTO.nickName());
        assertThat(info.getAge()).isEqualTo(memberSignUpDTO.age());
        assertThat(info.getName()).isEqualTo(memberSignUpDTO.name());
    }

    @Test
    public void 내정보조회() throws Exception {
        //given
        MemberSignUpDTO memberSignUpDTO = setMember();
        //when
        MemberInfoDTO myInfo = memberService.getMyInfo();

        //then
        assertThat(myInfo.getUsername()).isEqualTo(memberSignUpDTO.username());
        assertThat(myInfo.getNickName()).isEqualTo(memberSignUpDTO.nickName());
        assertThat(myInfo.getAge()).isEqualTo(memberSignUpDTO.age());
        assertThat(myInfo.getName()).isEqualTo(memberSignUpDTO.name());

    }
}