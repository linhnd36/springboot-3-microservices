package com.example.department.service.controller;

import com.example.department.service.client.EmployeeClient;
import com.example.department.service.model.Department;
import com.example.department.service.model.Employee;
import com.example.department.service.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    private DepartmentRepository repository;

//    @Autowired
//    private EmployeeClient employeeClient;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public Department add(@RequestBody Department department) {
        LOGGER.info("Department add: {}", department);
        return repository.addDepartment(department);
    }

    @GetMapping
    public List<Department> findAll() {
        LOGGER.info("Department find");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Department findById(@PathVariable Long id) {
        LOGGER.info("Department find: id={}", id);
        return repository.findById(id);
    }

    @GetMapping("/with-employees")
    public List<Department> findAllWithEmployees() {
        LOGGER.info("Department find");
        List<Department> departments
                = repository.findAll();
//        departments.forEach(department ->
//                department.setEmployees(
//                        employeeClient.findByDepartment(department.getId())));
        for (Department department : departments){
            ResponseEntity<List<Employee>> response = restTemplate.exchange(
                    "http://employee-service/employee/department/" + department.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Employee>>() {}
            );
            department.setEmployees(response.getBody());
        }
        return departments;
    }

}
