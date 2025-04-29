package com.group02.repository;

public interface Updatable<T> {
    boolean update(T entity);
}