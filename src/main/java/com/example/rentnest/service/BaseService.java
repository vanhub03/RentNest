package com.example.rentnest.service;

import com.example.rentnest.model.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseEntity, ID extends Serializable> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(ID id);
}