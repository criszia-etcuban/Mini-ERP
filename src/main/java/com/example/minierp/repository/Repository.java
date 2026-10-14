package com.example.minierp.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Repository<T> {
    private HashMap<String, T> storage = new HashMap<>();

    public void add(String id, T entity) {
        storage.put(id, entity);
    }

    public T findById(String id) {
        return storage.get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    public void remove(String id) {
        storage.remove(id);
    }

    public int count() {
        return storage.size();
    }

    public Optional<T> find(String id) {
        return Optional.ofNullable(storage.get(id));
    }
}