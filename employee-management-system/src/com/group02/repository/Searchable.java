package com.group02.repository;

import java.util.List;
import java.util.Map;

public interface Searchable<T> {
    List<T> search(Map<String, Object> criteria);
}
