package board.myboard.domain.crawl.util;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class JsoupRun {

    public static void main(String[] args) {

        try {
            // input은 여기만 바꿔주면 된다!
            JsoupWebCrawler webCrawler = new JsoupWebCrawler();
            String url = "https://naver.com";
            String inputElementSelector = "input";

            // JsoupWebCrawler 객체를 통해 연결된 Document 객체 가져오기
            Document doc = webCrawler.getJsoupConnecttionDoc(url); //URL에 접속해서 HTML 문서를 가져옴
            // 선택자로 원하는 요소를 가져오기
            Elements inputElements = webCrawler.getJsoupElements(doc, inputElementSelector); // 특정 CSS 선택자로 HTML 요소를 추출

            // 가져온 요소 출력
            System.out.println(inputElements);
            System.out.println(inputElements.attr("placeholder"));
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 출력하거나 다른 처리를 수행합니다.
        }
    }
}
