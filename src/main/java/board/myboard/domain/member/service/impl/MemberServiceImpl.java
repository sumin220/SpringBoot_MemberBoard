package board.myboard.domain.member.service.impl;

import board.myboard.domain.member.dto.MemberInfoDTO;
import board.myboard.domain.member.dto.MemberSignUpDTO;
import board.myboard.domain.member.dto.MemberUpdateDTO;
import board.myboard.domain.member.entity.Member;
import board.myboard.domain.member.exception.MemberException;
import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.domain.member.service.MemberService;
import board.myboard.global.exception.ErrorCode;
import board.myboard.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(MemberSignUpDTO memberSignUpDTO) throws Exception {

        Member member = memberSignUpDTO.toEntity();
        member.addUserAuthority();
        member.encodePassword(passwordEncoder);

        if (memberRepository.findByUsername(memberSignUpDTO.username()).isPresent()) {
            throw new MemberException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        memberRepository.save(member);
    }

    @Override
    public void update(MemberUpdateDTO memberUpdateDTO) throws Exception {

        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        memberUpdateDTO.age().ifPresent(member::updateAge);
        memberUpdateDTO.name().ifPresent(member::updateName);
        memberUpdateDTO.nickName().ifPresent(member::updateNickName);
    }

    @Override
    public void updatePassword(String checkPassword, String newPassword) throws Exception {

        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        if (!member.matchPassword(passwordEncoder, checkPassword)) {
            throw new MemberException(ErrorCode.MISMATCH_PASSWORD);
        }

        member.updatePassword(passwordEncoder, newPassword);
    }

    @Override
    public void withdraw(String checkPassword) throws Exception {

        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        if (!member.matchPassword(passwordEncoder, checkPassword)) {
            throw new MemberException(ErrorCode.MISMATCH_PASSWORD);
        }

        memberRepository.delete(member);

    }

    @Override
    public MemberInfoDTO getInfo(Long id) throws Exception {

        Member findMember = memberRepository.findById(id).orElseThrow(
                () -> new Exception("회원이 존재하지 않습니다."));
        return new MemberInfoDTO(findMember);
    }

    @Override
    public MemberInfoDTO getMyInfo() throws Exception {
        Member findMember = memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(
                () -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        return new MemberInfoDTO(findMember);
    }
}
