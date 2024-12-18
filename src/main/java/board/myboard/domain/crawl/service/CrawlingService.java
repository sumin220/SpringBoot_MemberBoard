package board.myboard.domain.crawl.service;


import board.myboard.domain.crawl.entity.CrawledData;
import board.myboard.domain.crawl.repository.CrawlRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final CrawlRepository repository;

    public void saveCrawledData(String url, String selector, Document document) {
        Elements elements = document.select(selector);
        String content = elements.text();

        CrawledData data = new CrawledData(url, content);
        repository.save(data);

        System.out.println("데이터 저장 성공: URL=" + url);
    }
}

