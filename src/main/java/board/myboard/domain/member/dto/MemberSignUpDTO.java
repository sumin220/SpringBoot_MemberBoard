package board.myboard.domain.member.dto;

import board.myboard.domain.member.entity.Member;

public record MemberSignUpDTO(
        String username,
        String password,
        String name,
        String nickName,
        Integer age
) {

    public Member toEntity() {
        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .nickName(nickName)
                .age(age)
                .build();
    }
}
