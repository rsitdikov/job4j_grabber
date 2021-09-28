import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DateTimeParser;
import utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements elements = doc.select(".postslisttopic");
            for (Element el : elements) {
                link = el.child(0).attr("href");
                rsl.add(detail(link));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post detail(String link) {
        Post rsl = null;
        try {
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
            LocalDateTime created = dateTimeParser
                    .parse(dateTime);
            rsl = new Post(title, link, description, created);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public static void main(String[] args) {
            String url = "https://www.sql.ru/forum/job-offers";
            Parse parser = new SqlRuParse(new SqlRuDateTimeParser());
            List<Post> posts = parser.list(url);
            for (Post post : posts) {
                System.out.println(post.toString());
            }
    }
}