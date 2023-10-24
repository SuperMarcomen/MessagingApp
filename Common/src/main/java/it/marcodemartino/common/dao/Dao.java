package it.marcodemartino.common.dao;

import java.util.List;

public interface Dao<T> {

    List<T> getAll();
    void insert(T entity);
    void update(T entity);
    void delete(T entity);

}
