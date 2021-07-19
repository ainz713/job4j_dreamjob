package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {

    private static final Store INST = new Store();

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private Store() {
        posts.put(1, new Post(1, "Junior Java Job", "Здесь должно быть описание Джуна", "Вчера"));
        posts.put(2, new Post(2, "Middle Java Job", "Здесь должно быть описание Мидла", "Сегодня"));
        posts.put(3, new Post(3, "Senior Java Job", "Здесь должно быть описание Сеньора", "05-07-2021"));
    }

    public static Store instOf() {
        return INST;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
