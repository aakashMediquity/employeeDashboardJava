package com.example.employee;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private Map<Integer, Employee> db = new HashMap<>();

    @PostMapping
    public Employee create(@RequestBody Employee emp) {
        db.put(emp.getId(), emp);
        return emp;
    }

    @GetMapping
    public Collection<Employee> getAll() {
        return db.values();
    }
}