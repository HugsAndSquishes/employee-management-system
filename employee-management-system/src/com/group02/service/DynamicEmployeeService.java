package com.group02.service;

import com.group02.model.DynamicEmployee;
import java.util.Map;

public interface DynamicEmployeeService {
    boolean addColumn(String columnName, String dataType);

    Map<String, String> getTableColumns();

    DynamicEmployee getDynamicEmployeeById(int id);

    boolean updateDynamicEmployee(DynamicEmployee employee);
}