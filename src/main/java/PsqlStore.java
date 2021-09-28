import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO post (title, link, description, created)"
                + " VALUES ((?), (?), (?), (?))";
        try (PreparedStatement ps = cnn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (Statement stmt = cnn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM post;");
            while (rs.next()) {
                rsl.add(load(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(String id)  {
        Post rsl = null;
        String sql = String.format(
                "SELECT * FROM post WHERE id = %s;", id      
        );
        try (Statement stmt = cnn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                rsl = load(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;

    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private Post load(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("link"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream(
                "rabbit.properties")) {
            Properties cfg = new Properties();
            cfg.load(in);
            PsqlStore store = new PsqlStore(cfg);
            Post first = new Post("title1",
                    "link1",
                    "first post",
                    LocalDateTime.now().minusDays(1L));
            Post second = new Post("title2",
                    "link2",
                    "second post",
                    LocalDateTime.now());
            store.save(first);
            store.save(second);
            System.out.println("Result of applying 'getAll':");
            for (Post post : store.getAll()) {
                System.out.println(post.toString());
            }
            System.out.println("Result of applying 'findById':");
            Post found = store.findById("2");
            System.out.println(found.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
