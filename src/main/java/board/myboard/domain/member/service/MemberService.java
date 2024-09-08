package board.myboard.domain.member.service;


import board.myboard.domain.member.dto.MemberInfoDTO;
import board.myboard.domain.member.dto.MemberSignUpDTO;
import board.myboard.domain.member.dto.MemberUpdateDTO;

public interface MemberService {

    //== 회원가입 ==//
    void signup(MemberSignUpDTO memberSignUpDTO) throws Exception;

    //== 정보수정 ==//
    void update(MemberUpdateDTO memberUpdateDTO) throws Exception;

    void updatePassword(String checkPassword, String newPassword) throws Exception;

    //== 회원탈퇴 ==//
    void withdraw(String checkPassword) throws Exception;

    //== 정보조회 ==//
    MemberInfoDTO getInfo(Long id) throws Exception;

    MemberInfoDTO getMyInfo() throws Exception;

}
