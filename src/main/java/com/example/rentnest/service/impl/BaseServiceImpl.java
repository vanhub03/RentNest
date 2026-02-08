package com.example.rentnest.service.impl;

import com.example.rentnest.model.BaseEntity;
import com.example.rentnest.repository.BaseRepository;
import com.example.rentnest.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class BaseServiceImpl<T extends BaseEntity, ID extends Serializable, R extends BaseRepository<T, ID>> implements BaseService<T, ID> {

    @Autowired
    protected R repository;

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(T entity) {repository.delete(entity); }


    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
