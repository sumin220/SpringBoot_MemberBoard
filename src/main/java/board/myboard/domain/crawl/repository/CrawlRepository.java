package board.myboard.domain.crawl.repository;

import board.myboard.domain.crawl.entity.CrawledData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlRepository extends JpaRepository<CrawledData, Long> {

    // TODO 셀레니움 how?
}
