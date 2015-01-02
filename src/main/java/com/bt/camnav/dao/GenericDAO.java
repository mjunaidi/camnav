package com.bt.camnav.dao;

import java.util.List;

public interface GenericDAO<T> {
    public T save(T object);

    public T update(T object);

    public T get(Long id);

    public T get(String name);

    public void delete(Long id);

    public void delete(T object);
    
    public int deleteAll();

    public List<T> list();

    public List<T> list(int first, int size);

    public List<T> search(String name, int first, int size);
    
    public Long countRow();
}
