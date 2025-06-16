package at.fhburgenland.dao;

import java.util.List;

public interface Crud<T, ID> {
    List<T> findAll();
    T findById(ID id);

    T save(T entity);

    T update(T entity);

    void delete(T entity);
    boolean deleteById(ID id);

    List<T> query(String query);
}
