import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqlRuParse {

    public Post detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        String title = doc.select(".messageHeader")
                .first()
                .text();
        String description = doc.select(".msgBody")
                .get(1)
                .text();
        String dateTime = doc.select(".msgFooter")
                .first()
                .text()
                .split(" \\[")[0];
        LocalDateTime created = new SqlRuDateTimeParser()
                .parse(dateTime);
        return new Post(
                title, link, description, created
        );
    }

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
        System.out.println("Post details: ");
        System.out.println(new SqlRuParse()
                .detail("https://www.sql.ru/forum/1325330"
                        + "/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t")
                .toString());
    }
}