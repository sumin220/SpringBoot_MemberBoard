package board.myboard.domain.post.repository;

import board.myboard.domain.post.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * EntityGraph는 페치 조인을 간편하게 사용할 수 있도록 해주는 어노테이션이다.
     * "select p from Post p join fetch p.writer w where p.id=:id"
     */
    @EntityGraph(attributePaths = {"writer"})
    Optional<Post> findWithWriterById(Long id);
}
