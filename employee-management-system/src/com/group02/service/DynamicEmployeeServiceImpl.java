package com.group02.service;

import com.group02.model.DynamicEmployee;
import com.group02.repository.DynamicEmployeeManager;

import java.util.Map;

public class DynamicEmployeeServiceImpl implements DynamicEmployeeService {
    private DynamicEmployeeManager dynamicEmployeeManager;

    public DynamicEmployeeServiceImpl() {
        this.dynamicEmployeeManager = new DynamicEmployeeManager();
    }

    @Override
    public boolean addColumn(String columnName, String dataType) {
        return dynamicEmployeeManager.addColumn(columnName, dataType);
    }

    @Override
    public Map<String, String> getTableColumns() {
        return dynamicEmployeeManager.getTableColumns();
    }

    @Override
    public DynamicEmployee getDynamicEmployeeById(int id) {
        return dynamicEmployeeManager.getDynamicEmployeeById(id);
    }

    @Override
    public boolean updateDynamicEmployee(DynamicEmployee employee) {
        return dynamicEmployeeManager.updateDynamicEmployee(employee);
    }
}