package board.myboard.domain.crawl.controller;

import board.myboard.domain.crawl.service.CrawlingService;
import board.myboard.domain.crawl.util.JsoupWebCrawler;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlingController {

    private final JsoupWebCrawler webCrawler;
    private final CrawlingService crawlingService;


    @PostMapping
    public String crawlAndSave(@RequestParam String url, @RequestParam String selector) {
        try {
            Document document = webCrawler.getJsoupConnecttionDoc(url);
            crawlingService.saveCrawledData(url, selector, document);
            return "크롤링 및 데이터 저장 성공";
        } catch (Exception e) {
            return "크롤링 실패: " + e.getMessage();
        }
    }
}
