import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import utils.SqlRuDateTimeParser;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (InputStream in = new FileInputStream(new File("./src/main/resources/app.properties"))) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        data.put("baseUri", cfg.getProperty("baseUri"));
        data.put("pages", cfg.getProperty("pages"));
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        SimpleTrigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            Integer pages = map.getIntegerFromString("pages");
            String baseUri = map.getString("baseUri");
            try {
                IntStream
                        .rangeClosed(1, pages)
                        .mapToObj(page -> String.format("%s/%s", baseUri, page))
                        .map(parse::list)
                        .flatMap(List::stream)
                        .filter(post -> post.getTitle().toLowerCase().contains("java "))
                        .forEach(post -> {
                            store.save(post);
                            System.out.println(post.getTitle());
                            System.out.println(post.getLink());
                            System.out.println(post.getCreated().format(
                                    DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"))
                            );
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isExists(Post post, List<Post> posts) {
            return posts.stream()
                    .anyMatch(p -> post.getLink().equals(p.getLink()));
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(new SqlRuDateTimeParser()), store, scheduler);
    }
}
