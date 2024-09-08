package board.myboard.domain.member.controller;

import board.myboard.domain.member.dto.*;
import board.myboard.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void singUp(@Valid @RequestBody MemberSignUpDTO memberSignUpDTO) throws Exception {
        memberService.signup(memberSignUpDTO);
    }

    /**
     * 회원정보수정
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateInfo(@Valid @RequestBody MemberUpdateDTO memberUpdateDTO) throws Exception {
        memberService.update(memberUpdateDTO);
    }

    /**
     * 비밀번호수정
     */
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) throws Exception {
        memberService.updatePassword(updatePasswordDTO.checkPassword(), updatePasswordDTO.newPassword());
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@Valid @RequestBody MemberWithdrawDTO memberWithdrawDTO)throws Exception {
        memberService.withdraw(memberWithdrawDTO.checkPassword());
    }

    /**
     * 회원정보조회
     */
    @GetMapping("/{id}")
    public ResponseEntity getInfo(@Valid @PathVariable("id") Long id) throws Exception {
        MemberInfoDTO info = memberService.getInfo(id);
        return new ResponseEntity(info, HttpStatus.OK);
    }

    /**
     * 내정보조회
     */
    @GetMapping("/member")
    public ResponseEntity getMyInfo(HttpServletResponse response) throws Exception {

        MemberInfoDTO myInfo = memberService.getMyInfo();
        return new ResponseEntity(myInfo, HttpStatus.OK);
    }
}
