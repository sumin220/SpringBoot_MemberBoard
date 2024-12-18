package board.myboard.domain.crawl.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

// Jsoup 라이브러리를 통해 HTML 문서를 가져오고 원하는 데이터를 추출
// 크롤링 대상 사이트와 요소를 추상화하여 재사용 가능한 메서드를 제공
@Component
public class JsoupWebCrawler {
    private String url;

    // connect된 객체 반환 메서드
    public Document getJsoupConnecttionDoc(String url) throws Exception {
        return Jsoup.connect(url).get();
    }

    // doc에 select로 원하는 태그 고르기
    public Elements getJsoupElements(Document doc, String inputElementSelector) {
        return doc.select(inputElementSelector);
    }
}

