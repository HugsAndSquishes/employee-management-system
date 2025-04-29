package com.group02.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DynamicEmployee extends Employee {
    private Map<String, Object> dynamicAttributes;

    public DynamicEmployee() {
        super();
        this.dynamicAttributes = new HashMap<>();
    }

    public DynamicEmployee(int empID, String employeeName, String jobTitle,
            String division, BigDecimal salary, String payInfo) {
        super(empID, employeeName, jobTitle, division, salary, payInfo);
        this.dynamicAttributes = new HashMap<>();
    }

    public void addAttribute(String name, Object value) {
        dynamicAttributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return dynamicAttributes.get(name);
    }

    public Map<String, Object> getAllDynamicAttributes() {
        return new HashMap<>(dynamicAttributes);
    }

    public void removeAttribute(String name) {
        dynamicAttributes.remove(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nDynamic Attributes: ");

        if (dynamicAttributes.isEmpty()) {
            sb.append("None");
        } else {
            for (Map.Entry<String, Object> entry : dynamicAttributes.entrySet()) {
                sb.append("\n  ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
        }

        return sb.toString();
    }
}