package board.myboard.domain.member.dto;

import java.util.Optional;

public record MemberUpdateDTO(
        Optional<String> name,
        Optional<String> nickName,
        Optional<Integer> age
) {
}
