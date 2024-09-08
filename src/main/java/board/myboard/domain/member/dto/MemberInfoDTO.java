package board.myboard.domain.member.dto;

import board.myboard.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDTO {

    private String name;

    private String nickName;

    private String username;

    private Integer age;

    @Builder
    public MemberInfoDTO(Member member) {
        this.name = member.getName();
        this.nickName = member.getNickName();
        this.username = member.getUsername();
        this.age = member.getAge();
    }
}
