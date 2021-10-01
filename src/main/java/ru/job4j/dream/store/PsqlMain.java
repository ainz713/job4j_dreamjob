package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.save(new Post(0, "Java Job13"));
        store.save(new Candidate(0, "Can1"));
        store.save(new Candidate(1, "Can2"));
        store.save(new Candidate(0, "Can3"));
        store.save(new User(0, "123", "123@mail.ru", "123"));
        for (User user : store.findAllUsers()) {
            System.out.println(user.getId() + " " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
        }
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }
    }
}
