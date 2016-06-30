package urstory.ganada;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by urstory on 2016. 6. 30..
 */
public class GanadaMain {
    public static void main(String args[]){
        try {
            String ganadaListPagePrefix = "http://www.korean.go.kr/front/mcfaq/mcfaqList.do?mn_id=62&pageIndex=";
            List<GanadaPage> list = new ArrayList<>();
            for (int pageIndex = 1; pageIndex <= 495; pageIndex++) {
                Document doc = Jsoup.connect(ganadaListPagePrefix + pageIndex).get();
                Elements nthChildElements = doc.select("#mcfaqForm > table > tbody > tr");
                int trCount = nthChildElements.size();
                Elements aElements = doc.select("#mcfaqForm > table > tbody > tr:nth-child(1) > td.align_l.mobile_view.b_tit > a");
                String javascript = aElements.get(0).attr("href");
                String num = javascript.substring(javascript.indexOf("'") + 1, javascript.lastIndexOf("'"));
                String detailPage = "http://www.korean.go.kr/front/mcfaq/mcfaqView.do?mn_id=62&mcfaq_seq=" + num + "&pageIndex=" + pageIndex;
                String title = aElements.get(0).text();
                Document detailDoc = Jsoup.connect(detailPage).get();
                Elements questionElements = detailDoc.select("#mcfaqViewForm > div.board_view > div.b_view_content.b_line_dot");
                String questionHtml = questionElements.get(0).html();
                String questionStr = questionElements.get(0).text();
                Elements answerElements = detailDoc.select("#mcfaqViewForm > div.board_view > div:nth-child(4)");
                String answerHtml = answerElements.get(0).html();
                String answerStr = answerElements.get(0).text();
                GanadaPage ganadaPage = new GanadaPage();
                ganadaPage.setUrl(detailPage);
                ganadaPage.setTitle(title);
                ganadaPage.setAnswer(answerStr);
                ganadaPage.setAnswerHtml(answerHtml);
                ganadaPage.setQuestion(questionStr);
                ganadaPage.setQuestionHtml(questionHtml);
                list.add(ganadaPage);
                System.out.println(pageIndex);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("ganada.json"), list);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

//#mcfaqForm > table > tbody > tr:nth-child(1) > td.align_l.mobile_view.b_tit > a
//#mcfaqForm > table > tbody > tr:nth-child(2) > td.align_l.mobile_view.b_tit > a
//
//        #mcfaqForm > table > tbody > tr:nth-child
// #mcfaqViewForm > div.board_view > div.b_view_content.b_line_dot > p
// #mcfaqViewForm > div.board_view > div:nth-child(4) > p