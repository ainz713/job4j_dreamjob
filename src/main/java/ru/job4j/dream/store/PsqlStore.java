package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsqlStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("Записей не найдено", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM candidates")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    Candidate can = new Candidate(
                            it.getInt("id"),
                            it.getString("name"));
                    can.setCityId(it.getInt("city_id"));
                    candidates.add(can);
                }
            }
        } catch (Exception e) {
            LOG.error("Кандидатов не найдено", e);
        }
        return candidates;
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM users")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(new User(it.getInt("id"), it.getString("name"),
                            it.getString("email"), it.getString("password")));
                }
            }
        } catch (Exception e) {
            LOG.error("Пользователей не найдено", e);
        }
        return users;
    }

    @Override
    public Collection<City> findAllCities() {
        List<City> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    cities.add(
                            new City(
                                    it.getInt("id"),
                                    it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return cities;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    @Override
    public void removePost(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("delete from post where id = ?")) {
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            LOG.error("Ошибка удаления записи", e);
        }
    }

    @Override
    public void removeCandidate(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("delete from candidates where id = ?",
                             PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            LOG.error("Ошибка удаления кандидата", e);
        }
    }

    @Override
    public void removeUser(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("delete from users where id = ?",
                             PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            LOG.error("Ошибка удаления пользователя", e);
        }
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO post(name) VALUES (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка создания записи", e);
        }
        return post;
    }

    private Candidate create(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO candidates(name, city_id) VALUES (?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCityId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка создания кандидата", e);
        }
        return candidate;
    }

    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO users(name, email, password) "
                             + "VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка создания пользователя", e);
        }
        return user;
    }

    private void update(Post post) {
        try (Connection cn = pool.getConnection();
                PreparedStatement statement =
                     cn.prepareStatement("update post set name = ? where id = ?")) {
            statement.setString(1, post.getName());
            statement.setInt(2, post.getId());
            statement.execute();
        } catch (Exception e) {
            LOG.error("Ошибка обновления записи", e);
        }
    }

    private void update(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("update users set name = ?, email = ?, password = ? where id = ?")) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getId());
            statement.execute();
        } catch (Exception e) {
            LOG.error("Ошибка обновления кандидата", e);
        }
    }

    private void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("update candidates set name = ?, city_id = ? where id = ?")) {
            statement.setString(1, candidate.getName());
            statement.setInt(2, candidate.getCityId());
            statement.setInt(3, candidate.getId());
            statement.execute();
        } catch (Exception e) {
            LOG.error("Ошибка обновления кандидата", e);
        }
    }

    @Override
    public Post findById(int id) {
        try (Connection cn = pool.getConnection();
                PreparedStatement statement =
                     cn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            }
        } catch (SQLException throwables) {
            LOG.error("Запись не найдена", throwables);
        }
        return null;
    }

    @Override
    public Candidate findCanById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("select * from candidates where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Candidate(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            }
        } catch (SQLException throwables) {
            LOG.error("Кандидат не найден", throwables);
        }
        return null;
    }

    @Override
    public User findUserById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("select * from users where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException throwables) {
            LOG.error("Кандидат не найден", throwables);
        }
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("select * from users where email = ?")) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException throwables) {
            LOG.error("Кандидат не найден", throwables);
        }
        return null;
    }

    @Override
    public Collection<Post> findLastDayPosts() {
        List<Post> lastDayPosts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT * FROM post WHERE created "
                             + "BETWEEN current_timestamp - interval '1 day' AND current_timestamp")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    lastDayPosts.add(new Post(it.getInt("id"),
                            it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("EXCEPTION: ", e);
        }
        return lastDayPosts;
    }

    @Override
    public Collection<Candidate> findLastDayCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM Candidate "
                     + "WHERE registered BETWEEN current_timestamp - interval '1 day' AND current_timestamp")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    Candidate can = new Candidate(
                            it.getInt("id"),
                            it.getString("name"));
                    can.setCityId(it.getInt("city_id"));
                    candidates.add(can);
                }
            }
        } catch (Exception e) {
            LOG.error("Кандидатов не найдено", e);
        }
        return candidates;
    }
}
