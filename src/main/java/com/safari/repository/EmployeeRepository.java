package com.safari.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safari.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

}
