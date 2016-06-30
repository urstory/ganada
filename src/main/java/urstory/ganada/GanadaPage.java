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
