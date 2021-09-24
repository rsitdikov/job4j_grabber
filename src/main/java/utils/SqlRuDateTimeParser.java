package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.StringJoiner;

import static java.util.Map.entry;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "01"),
            entry("фев", "02"),
            entry("мар", "03"),
            entry("апр", "04"),
            entry("май", "05"),
            entry("июн", "06"),
            entry("июл", "07"),
            entry("авг", "08"),
            entry("сен", "09"),
            entry("окт", "10"),
            entry("ноя", "11"),
            entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String parse) {
        String[] dateTime = parse.split(",");
        return LocalDateTime.of(
                parseDate(dateTime[0]),
                parseTime(dateTime[1])
        );
    }

    private LocalDate parseDate(String parse) {
        LocalDate rsl;
        if (parse.contains("вчера")) {
            rsl = LocalDate.now().minusDays(1L);
        } else if (parse.contains("сегодня")) {
            rsl = LocalDate.now();
        } else {
            String[] values = parse.split(" ");
            values[1] = MONTHS.get(values[1]);
            parse = new StringJoiner("-")
                    .add(values[0])
                    .add(values[1])
                    .add(values[2])
                    .toString();
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("d-MM-yy");
            rsl = LocalDate.parse(parse, formatter);
        }
        return rsl;
    }

    private LocalTime parseTime(String parse) {
        DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern("H:mm");
        return LocalTime.parse(
                parse.replaceFirst(" ", ""),
                formatter
        );
    }
}