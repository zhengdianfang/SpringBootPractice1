package com.zhengdianfang.springbootpractice1;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = loadEmployeeListFromFile();
        return employees;
    }

    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody Employee employee) {
        List<Employee> employees = loadEmployeeListFromFile();
        employees.add(employee);
        writeEmployeesToFile(employees);
        return employee;
    }

    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@PathVariable("id") long id,  @RequestBody Employee employee) {
        List<Employee> employees = loadEmployeeListFromFile();
        Employee needUpdateEmployee = employees.stream().filter(elem -> elem.getId() == id).findFirst().orElseGet(null);
        if (needUpdateEmployee != null) {
            needUpdateEmployee.setAge(employee.getAge());
            needUpdateEmployee.setName(employee.getName());
            needUpdateEmployee.setGender(employee.getGender());
        }
        writeEmployeesToFile(employees);
        return needUpdateEmployee;
    }

    @DeleteMapping("/employees/{id}")
    public void deleteEmployee(@PathVariable("id") long id) {
        List<Employee> employees = loadEmployeeListFromFile();
        List<Employee> filterEmployees = employees.stream().filter(elem -> elem.getId() != id).collect(Collectors.toList());
        writeEmployeesToFile(filterEmployees);
    }

    private List<Employee> loadEmployeeListFromFile() {
        List<Employee> employees = null;
        Resource resource = new ClassPathResource("data.json");
        try {
            InputStream resourceInputStream = resource.getInputStream();
            employees = objectMapper.readValue(
                    resourceInputStream,
                    objectMapper.getTypeFactory().constructParametricType(List.class, Employee.class)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private void writeEmployeesToFile(List<Employee> employees) {
        Resource resource = new ClassPathResource("data.json");
        try {
            objectMapper.writeValue(new FileOutputStream(resource.getFile()), employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
