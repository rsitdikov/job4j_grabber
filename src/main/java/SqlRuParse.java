import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.SqlRuDateTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqlRuParse {

    public static void main(String[] args) throws Exception {
        int pages = 5;
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd-MM-yy HH:mm");
        for (int page = 1; page <= pages; page++) {
            System.out.printf("Page :%s%s", page, System.lineSeparator());
            String url = String.format(
                    "https://www.sql.ru/forum/job-offers/%s", page
            );
            Document doc = Jsoup.connect(url).get();
            Elements row = doc.select(".postslisttopic");
            Elements dates = doc.select("td[style].altCol");
            int index = 0;
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                LocalDateTime date = new SqlRuDateTimeParser()
                        .parse(dates.get(index++).text());
                System.out.println(date.format(formatter));
            }
        }
    }
}