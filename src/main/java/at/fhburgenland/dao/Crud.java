package at.fhburgenland.dao;

import jakarta.persistence.Tuple;

import java.util.List;
import java.util.Map;

public interface Crud<T, ID> {
    List<T> findAll();

    T findById(ID id);

    T save(T entity);

    T update(T entity);

    boolean deleteById(ID id);

    List<Tuple> query(String query, Map<String, Object> parameters);
}
