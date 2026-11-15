package com.econest.dao;

import java.sql.SQLException;
import java.util.List;

// Basic operations every "manage ..." screen needs
public interface CrudDAO<T> {
    void add(T item) throws SQLException;
    void update(T item) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> findAll() throws SQLException;
    T findById(int id) throws SQLException;
}
