package board.myboard.domain.member.dto;

import board.myboard.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberInfoDTO {

    private final String name;

    private final String nickName;

    private final String username;

    private final Integer age;

    @Builder
    public MemberInfoDTO(Member member) {
        this.name = member.getName();
        this.nickName = member.getNickName();
        this.username = member.getUsername();
        this.age = member.getAge();
    }
}
