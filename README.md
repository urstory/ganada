오늘 별거 아닌 메시지(?)가 하나 도착했습니다.

글에 맞춤법이 틀렸다는.... 메시지... 몇일이 아니라 며칠이라는...

너무 부끄러웠습니다. 메시지가 부끄러운 것이 아니라 공개된 동영상에 그런 문제가 있었다니.......

그리고 구글에서 검색을 해보니 네이버 우리말 바로 쓰기에도 나오네요. 며칠이 올바른 표현이라고..... 요다는 네이버에서 우리말 바로쓰기에서 일했던 걸까요?

네이버 우리말 바로쓰기의 데이터는 국립어학원 온라인 가나다 상담사례 모음 중 일부분이었습니다.

[http://www.korean.go.kr/front/mcfaq/mcfaqList.do?mn_id=62](http://www.korean.go.kr/front/mcfaq/mcfaqList.do?mn_id=62)

위의 사이트에서 상담사례를 볼 수 있습니다.

html문서를 읽어오고 쉽게 파싱할 수 있는 라이브러리가 있습니다. css selector 를 이용하여 읽을 수 있죠.

[https://jsoup.org/](https://jsoup.org/)

사용법은 다음과 같습니다.

```
Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
Elements newsHeadlines = doc.select("#mp-itn b a");
```

위와 같은 명령이면.... 간단하게! selector문법으로 원하는 요소만 읽어올 수 있다는 것

그렇다면 기존 문서에서 내가 원하는 부분을 selector를 쉽게 구할 수 있는 방법이 없을까? 하는 생각에 지인에게 물어보았습니다.
역시 크롬에 이런 기능이 있더군요.

개발자 도구에서 요소를 선택한 후 우측버튼을 클릭하면 Copy - Copy selector라는 메뉴가 있더군요! 오오오.

크롤링한 데이터를 json데이터로 생성하기 위한 dto객체를 다음과 같이 작성합니다. text데이터와 html데이터를 별도로 저장하도록 합니다.

```
package urstory.ganada;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by urstory on 2016. 6. 30..
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GanadaPage {
    private String title;
    private String url;
    private String question;
    private String answer;
    private String questionHtml;
    private String answerHtml;
}
```

이제 url을 읽어들여 json데이터로 만들어 저장하는 클래스를 다음과 같이 작성합니다.

```
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
```

이제 실행. intelliJ실행 폴더에 생성된 ganada.json파일을 볼 수 있습니다.

```
[{"title":"'등산로'의 표준 발음","url":"http://www.korean.go.kr/front/mcfaq/mcfaqView.do?mn_id=62&mcfaq_seq=5536&pageIndex=1","question":"[질문] 자음동화 규칙에 의하여 'ㄴ'은 'ㄹ'의 앞이나 뒤에서 'ㄹ'로 소리 난다고 되어 있습니다. 그런데 '등산로' 등은 '등산노'로
 발음되는 것이 일반적입니다. 그 이유는 무엇입니까？","answer":"[답변] 자음동화 규칙의 보편성 문제를 질문하신 것으로 보입니다. 우선 'ㄹ'에 관련된 자음동화 현상을 보면 다음과 같습니다. (1) 'ㄹ'이 선행하는 음절 종성 'ㄱ, ㄷ, ㅂ, ㅁ, ㅇ'에 이어 날 때 'ㄴ'으로 바뀌>는 경우   목로 →[몽노], 몇량 →[면냥], 협력 →[혐녁] 감로 →[감노], 종로 →[종노]   (2) 'ㄴ'이 'ㄹ'의 앞이나 뒤에서 'ㄹ'로 발음되는 경우   신라 →[실라], 칼날 →[칼랄]   규칙 (2)에 의하면 '등산로'는 [등살로]가 되어야 할 텐데 그렇지 않습니다. 실제 요즘 발음을 보면 개>인차가 있기는 하나 젊은 세대에서는 특히 어휘에서 'ㄴ＋ㄹ'의 연결을 [ㄴㄴ]으로 발음하는 경향이 강한 듯합니다. 그 이유는 형태 보존의 심리에서 찾을 수 있을 것 같습니다. 즉 '등산로'의 경우 선행 형태소 '등산'이 자립형태소로 그 뜻이 분명한데, 형태를 바꿔 [등살]로 하>면 '등산'이란 의미와 거리감을 느끼기 때문에 규칙 (2)의 예외가 되면서 형태를 바꾸지 않는다는 설명이 가능합니다. 이러한 이유로 '등산'을 제대로 다 발음하고 나면 [등산노]가 되는데 이것은 규칙 (1)의 확대 적용으로 볼 수 있겠습니다. 'ㄴ＋ㄹ'은 [ㄹㄹ]로 바뀌는 것이 일>반적이고 [ㄴㄴ]으로 되는 경우도 있지만, 'ㄹ＋ㄴ'에서는 언제나 [ㄹ]로 동화되며 [ㄴㄴ]으로 발음되는 예가 없다는 사실을 보면 역행동화가 순행동화보다 제약되는 것이 아닌가 합니다. 〈표준 발음법〉에서는 다음과 같은 단어들을 규칙 ⑵에 대한 예외로 인정하고 있습니다. 의>견란[의:견난], 임진난[임:진난], 동원령[동:원녕] 상견례[상견녜], 결단력[결딴녁], 이원론[이: 원논]","questionHtml":"<p class=\"qna_tit_q\">[질문]</p> 자음동화 규칙에 의하여 'ㄴ'은 'ㄹ'의 앞이나 뒤에서 'ㄹ'로 소리 난다고 되어 있습니다. 그런데 '등산로' 등은 '등산노
'로 발음되는 것이 일반적입니다. 그 이유는 무엇입니까？","answerHtml":"<p class=\"qna_tit_a\">[답변]</p> \n<p>자음동화 규칙의 보편성 문제를 질문하신 것으로 보입니다. 우선 'ㄹ'에 관련된 자음동화 현상을 보면 다음과 같습니다.</p> \n<p>(1) 'ㄹ'이 선행하는 음절 종성
'ㄱ, ㄷ, ㅂ, ㅁ, ㅇ'에 이어 날 때 'ㄴ'으로 바뀌는 경우</p> \n<p>&nbsp;</p> \n<table border=\"1\" cellpadding=\"5\"> \n <tbody> \n  <tr> \n   <td>목로 →[몽노], 몇량 →[면냥], 협력 →[혐녁]<br>감로 →[감노], 종로 →[종노]</td> \n  </tr> \n </tbody> \n</table> \n<p>&nn
bsp;</p> \n<p>(2) 'ㄴ'이 'ㄹ'의 앞이나 뒤에서 'ㄹ'로 발음되는 경우</p> \n<p>&nbsp;</p> \n<table border=\"1\" cellpadding=\"5\"> \n <tbody> \n  <tr> \n   <td align=\"middle\">신라 →[실라], 칼날 →[칼랄]</td> \n  </tr> \n </tbody> \n</table> \n<p>&nbsp;</p> \n<p>>규칙 (2)에 의하면 '등산로'는 [등살로]가 되어야 할 텐데 그렇지 않습니다. 실제 요즘 발음을 보면 개인차가 있기는 하나 젊은 세대에서는 특히 어휘에서 'ㄴ＋ㄹ'의 연결을 [ㄴㄴ]으로 발음하는 경향이 강한 듯합니다. 그 이유는 형태 보존의 심리에서 찾을 수 있을 것 같습니다..
 즉 '등산로'의 경우 선행 형태소 '등산'이 자립형태소로 그 뜻이 분명한데, 형태를 바꿔 [등살]로 하면 '등산'이란 의미와 거리감을 느끼기 때문에 규칙 (2)의 예외가 되면서 형태를 바꾸지 않는다는 설명이 가능합니다. <br> 이러한 이유로 '등산'을 제대로 다 발음하고 나면 [등
산노]가 되는데 이것은 규칙 (1)의 확대 적용으로 볼 수 있겠습니다. 'ㄴ＋ㄹ'은 [ㄹㄹ]로 바뀌는 것이 일반적이고 [ㄴㄴ]으로 되는 경우도 있지만, 'ㄹ＋ㄴ'에서는 언제나 [ㄹ]로 동화되며 [ㄴㄴ]으로 발음되는 예가 없다는 사실을 보면 역행동화가 순행동화보다 제약되는 것이 아
닌가 합니다. <br>〈표준 발음법〉에서는 다음과 같은 단어들을 규칙 ⑵에 대한 예외로 인정하고 있습니다.</p> \n<table border=\"1\" cellpadding=\"5\"> \n <tbody> \n  <tr> \n   <td align=\"middle\">의견란[의:견난], 임진난[임:진난], 동원령[동:원녕]<br>상견례[상견녜], >결단력[결딴녁], 이원론[이: 원논
```

위의 내용은 크롤링하여 만든 json 파일의 일부분 내용입니다. 완전한 json파일은 첨부하였습니다.

이제 해당 데이터를 가지고 메신저 봇을 만들면 좋을 것 같습니다. 하루에 한번씩 가나다 내용 보여주기 봇이라고 해야할까요?
