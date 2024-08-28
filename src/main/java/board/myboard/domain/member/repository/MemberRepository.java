package board.myboard.domain.member.repository;

import board.myboard.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);

    boolean existsByUsername(String username);
}
