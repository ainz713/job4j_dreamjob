package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;

public interface Store {
    Collection<Post> findAllPosts();

    Collection<Candidate> findAllCandidates();

    Collection<User> findAllUsers();

    Collection<City> findAllCities();

    Collection<Post> findLastDayPosts();

    Collection<Candidate> findLastDayCandidates();

    void save(Post post);

    void save(Candidate candidate);

    void save(User user);

    Post findById(int id);

    Candidate findCanById(int id);

    User findUserById(int id);

    User findUserByEmail(String email);

    void removePost(int id);

    void removeUser(int id);

    void removeCandidate(int id);
}