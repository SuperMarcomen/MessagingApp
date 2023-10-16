package it.marcodemartino.common.dao;

import java.util.List;
import java.util.UUID;

public interface Dao<T> {

    T getByUUID(UUID uuid);
    List<T> getAll();
    void insert(T entity);
    void update(T entity);
    void delete(T entity);

}
